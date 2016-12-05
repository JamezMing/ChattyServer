package global;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;

public class MessageDataBaseTest {

	@Test
	public void testSearchItemInetAddressStringInteger(){
		ServerMessageDataBaseManager.init();
		try {
			
			ServerMessageDataBaseManager.insertItem(InetAddress.getByName("192.168.2.222"), "James", 3333, "Hello", InetAddress.getByName("192.168.2.222"), 4444, 0);
			String[] res = ServerMessageDataBaseManager.searchItem(InetAddress.getByName("192.168.2.222"), "James", 0);
			assertEquals(7, res.length);
		} catch (UnknownHostException | SqlJetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

}
