package jshm.wts.sh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;

import jshm.exceptions.ClientException;
import jshm.sh.Client;
import jshm.sh.RbPlatform;
import jshm.sh.client.HttpForm;
import jshm.wts.URLs;
import jshm.wts.WTGame;
import jshm.wts.WTScore;

public class Api {
	static final Logger LOG = Logger.getLogger(Api.class.getName());

	static final Pattern ERROR_PATTERN =
		Pattern.compile("^.*<span class=\"error\">\\s*(.*?)\\s*</(?:span|td)>.*$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	
	public static void submitWTScore(final WTScore score) throws Exception {
		Client.getAuthCookies();
		
		String[] staticData = {
			"song", String.valueOf(score.getSong().scoreHeroId),
			"game", String.valueOf(WTGame.getId(score.getPlatform())),
			"platform", String.valueOf(RbPlatform.getId(score.getPlatform())),
			"group", String.valueOf(WTGame.scoreHeroGroupId),
			"score", String.valueOf(score.getScore()),
			"rating", score.getRating() != 0 ? String.valueOf(score.getRating()) : "",
			"comment", score.getComment(),
			"link", score.getImageUrl(),
			"videolink", score.getVideoUrl()	
		};
		
		List<String> data = new ArrayList<String>(Arrays.asList(staticData));

		final String istr = score.getInstrument().formAbbr;
		data.add(istr + "Diff");
		data.add(String.valueOf(score.getDifficulty().scoreHeroId));
		data.add(istr + "Percent");
		data.add(score.getPercent() != 0 ? String.valueOf(score.getPercent()) : "");
		data.add(istr + "Streak");
		data.add(score.getStreak() != 0 ? String.valueOf(score.getStreak()) : "");
		
		
		new HttpForm((Object) URLs.getInsertScoreUrl(score), data) {
			public void afterSubmit(final int response, final HttpClient client, final HttpMethod method) throws Exception {
				String body = method.getResponseBodyAsString();
				
//				LOG.finest("submitRbScore() result body:");
//				LOG.finest("\n" + body);
				method.releaseConnection();
				
				Matcher m = ERROR_PATTERN.matcher(body);
				
				if (m.matches()) {
					Exception e = new ClientException(m.group(1));
					LOG.throwing("Api", "submitRbScore", e);
					throw e;
				}
				
				// can't be completely sure about this		
				if (!body.contains("window.close()")) {
					LOG.warning("Score may not have been accepted, response body follows:");
					LOG.warning(body);
					
					throw new ClientException("Score may not have been accepted");
				}
			}
		}.submit();
	}
}
