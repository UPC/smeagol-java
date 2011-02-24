import java.util.Collection;

import edu.upc.cpl.smeagol.client.SmeagolClient;
import edu.upc.cpl.smeagol.client.domain.Tag;

/**
 * Una classe per fer proves informals contra un servidor real.
 * 
 * @author angel
 * 
 */
public class Proves {

	public static void main(String[] args) throws Exception {
		int quantsAbans;
		int quantsDespres;

		SmeagolClient client = new SmeagolClient("http://localhost:3000/");

		Collection<Tag> tags = client.getTags();
		quantsAbans = tags.size();

		for (Tag t : tags) {
			log(t.toString());
		}

		client.createTag("TEST", "TEST DESCRIPTION");
		client.deleteTag("angel");
		quantsDespres = client.getTags().size();

		log("abans: " + quantsAbans + " despres: " + quantsDespres);

	}

	public static void log(String msg) {
		System.out.println(msg);
	}

}
