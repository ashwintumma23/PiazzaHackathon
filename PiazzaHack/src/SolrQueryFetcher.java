import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.json.JSONException;
import org.json.JSONObject;

public class SolrQueryFetcher {

	public static String fetchDocuments(String tags, int price)
			throws SolrServerException, UnsupportedEncodingException {
		String serverUrl ="http://localhost:8983/solr/research";
		SolrServer server = new HttpSolrServer(
				serverUrl);

		String[] tagsData = tags.split(",");
		StringBuffer finalQueryTagData = new StringBuffer();

		for (int index = 0; index < tagsData.length - 1; index++) {
			finalQueryTagData.append("facility:*" + tagsData[index].trim()+"*"
					+ " AND ");
		}
		finalQueryTagData.append("facility:*" + tagsData[tagsData.length - 1]+"*");

		SolrQuery query = new SolrQuery();
		String tagQuery = "( " + finalQueryTagData.toString() + " )";

		tagQuery += " AND price:[0 TO " + price + "]";
		
		String url = serverUrl+"/select?q="+URLEncoder.encode(tagQuery, "UTF-8")+"&wt=json";
		try {
			return getJson(url);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*query.setQuery(tagQuery);
		QueryResponse queryResponse;
		SolrDocumentList solrDocumentList;

		queryResponse = server.query(query);
		solrDocumentList = queryResponse.getResults();
		Iterator<SolrDocument> solrIterator = solrDocumentList.iterator();

		while (solrIterator.hasNext()) {
			SolrDocument solrDocument = solrIterator.next();
			System.out.println(solrDocument.toString());
		}*/
		return null;
	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public static JSONObject readJsonFromUrl(String url) throws IOException,
			JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is,
					Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}

	public static String getJson(String url) throws IOException, JSONException {
		JSONObject json = readJsonFromUrl(url);
		return json.toString();
	
	}
}
