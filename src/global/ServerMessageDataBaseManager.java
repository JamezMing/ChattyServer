package global;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;

import javax.xml.bind.DatatypeConverter;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

public class ServerMessageDataBaseManager {
	private static SqlJetDb db;
	private static final String DB_NAME = "ServerMessageData.db";
	private static final String TABLE_NAME = "ServerMessage";
	private static final String USER_ADDRESS_FIELD = "user_addr";
	private static final String USER_NAME_FIELD = "user_name";
	private static final String USER_RECEIVEPORT_FIELD = "user_receive_port";
	private static final String MESSAGE_FIELD = "user_message";
	private static final String MESSAGE_INDEX_FIELD = "message_index";
	private static final String MESSAGEQUERY_INDEX = "query_index";
	public ServerMessageDataBaseManager(){}
		
	public void init(){
		File dbFile = new File(DB_NAME);
		System.out.println(dbFile.exists());
		if(dbFile.exists()){
			try {
				db = SqlJetDb.open(dbFile, true);
				db.beginTransaction(SqlJetTransactionMode.WRITE);
				db.getOptions().setUserVersion(1);
			    db.commit();
			} catch (SqlJetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			try {
				db = SqlJetDb.open(dbFile, true);
				db.getOptions().setAutovacuum(true);
				db.beginTransaction(SqlJetTransactionMode.WRITE);
				db.getOptions().setUserVersion(1);
	            String createTableQuery = "CREATE TABLE " + TABLE_NAME + 
	            		" (" + USER_ADDRESS_FIELD + " TEXT NOT NULL, "+ USER_NAME_FIELD + " TEXT NOT NULL , " +  USER_RECEIVEPORT_FIELD + " TEXT NOT NULL , " + MESSAGE_FIELD + "TEXT NOT NULL, " + 
	            		MESSAGE_INDEX_FIELD + " PRIMARY KEY TEXT NOT NULL" +  ")";
	            String createNameQuery = "CREATE UNIQUE INDEX " + MESSAGEQUERY_INDEX + " ON " + TABLE_NAME + "(" +  USER_ADDRESS_FIELD + ", " + USER_RECEIVEPORT_FIELD  + ", " + MESSAGE_INDEX_FIELD + ")"; 
				db.createTable(createTableQuery);
				db.createIndex(createNameQuery);
				db.commit();
			} catch (SqlJetException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}}
		
		}
		
		public static void insertItem(InetAddress addr, String userName, Integer recPort, boolean isAvaliable, byte[] userKey, ArrayList<String> allowedListUser) throws SqlJetException{
			String address = addr.getHostAddress();
			String port = recPort.toString();
			db.beginTransaction(SqlJetTransactionMode.WRITE);
			ISqlJetTable table = db.getTable(TABLE_NAME);
			table.insert(address, userName, port, String.valueOf(isAvaliable), DatatypeConverter.printHexBinary(userKey), allowedListUser.toString());
		}
	
	
}
