package global;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.DatatypeConverter;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

import requestsParser.Request;

public class ServerMessageDataBaseManager {
	private static SqlJetDb db;
	private static final String DB_NAME = "ServerMessageData.db";
	private static final String TABLE_NAME = "ServerMessage";
	private static final String USER_ADDRESS_FIELD = "user_addr";
	private static final String USER_NAME_FIELD = "user_name";
	private static final String USER_RECEIVEPORT_FIELD = "user_receive_port";
	private static final String MESSAGE_FIELD = "user_message";
	private static final String MESSAGE_SENDER_ADDRESS = "sender_address";
	private static final String MESSAGE_SENDER_PORT = "sender_port";
	private static final String MESSAGE_INDEX_FIELD = "message_index";
	private static final String MESSAGEQUERY_INDEX = "query_index";
	private static final String NAMEMESSAGEQUERY_INDEX = "namequery_index";
	private static final String USER_INDEX = "user_index";
	private static final String PORTUSER_INDEX = "portuser_index";
	private ServerMessageDataBaseManager(){}
		
	public static void init(){
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
	            		MESSAGE_SENDER_ADDRESS + " TEXT NOT NULL, "+ MESSAGE_SENDER_PORT + " TEXT NOT NULL, " + MESSAGE_INDEX_FIELD + " PRIMARY KEY TEXT NOT NULL" +  ")";
	            
	            String createNameQuery = "CREATE UNIQUE INDEX " + MESSAGEQUERY_INDEX + " ON " + TABLE_NAME + "(" +  USER_ADDRESS_FIELD + ", " + USER_RECEIVEPORT_FIELD  + ", " + MESSAGE_INDEX_FIELD + ")"; 
	            String createMessageQuery = "CREATE UNIQUE INDEX " + NAMEMESSAGEQUERY_INDEX + " ON " + TABLE_NAME + "(" +  USER_ADDRESS_FIELD + ", " + USER_NAME_FIELD  + ", " + MESSAGE_INDEX_FIELD + ")";
	            String createUserQuery = "CREATE UNIQUE INDEX " + USER_INDEX + " ON " + TABLE_NAME + "(" +  USER_ADDRESS_FIELD + ", " + USER_NAME_FIELD   + ")";
	            String createPortUserQuery = "CREATE UNIQUE INDEX " + PORTUSER_INDEX + " ON " + TABLE_NAME + "(" +  USER_ADDRESS_FIELD + ", " + USER_RECEIVEPORT_FIELD   + ")";
				db.createTable(createTableQuery);
				db.createIndex(createNameQuery);
				db.createIndex(createMessageQuery);
				db.createIndex(createUserQuery);
				db.createIndex(createPortUserQuery);


				db.commit();
			} catch (SqlJetException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}}
		
		}
		
		
		
		public static String[] searchItem(InetAddress addr, Integer recPort, Integer messageIndex){
			try{
				db.beginTransaction(SqlJetTransactionMode.READ_ONLY);;
				ISqlJetCursor cursor = db.getTable(TABLE_NAME).lookup(MESSAGEQUERY_INDEX, addr.getHostAddress(), recPort.toString(), messageIndex.toString());
				System.out.println(new String("The record is found: \n Name: " + cursor.getString(USER_NAME_FIELD) + " \n" + "Address: " + cursor.getString(USER_ADDRESS_FIELD) + "\n" + 
						"User Receving Port: " + cursor.getString(USER_RECEIVEPORT_FIELD)));
				String[] res = {cursor.getString(USER_ADDRESS_FIELD), cursor.getString(USER_NAME_FIELD), cursor.getString(USER_RECEIVEPORT_FIELD), cursor.getString(MESSAGE_FIELD), cursor.getString(MESSAGE_SENDER_ADDRESS), cursor.getString(MESSAGE_SENDER_PORT), cursor.getString(MESSAGE_INDEX_FIELD)};
				db.commit();
				return res;
			}catch(SqlJetException e){
				System.out.println("An error in database has ocurred.");
				return null;
			}
		}
		public static String[] searchItem(InetAddress addr, String name, Integer messageIndex){
			try{
				db.beginTransaction(SqlJetTransactionMode.READ_ONLY);;
				ISqlJetCursor cursor = db.getTable(TABLE_NAME).lookup(MESSAGEQUERY_INDEX, addr.getHostAddress(), name, messageIndex.toString());
				System.out.println(new String("The record is found: \n Name: " + cursor.getString(USER_NAME_FIELD) + " \n" + "Address: " + cursor.getString(USER_ADDRESS_FIELD) + "\n" + 
						"User Receving Port: " + cursor.getString(USER_RECEIVEPORT_FIELD)));
				String[] res = {cursor.getString(USER_ADDRESS_FIELD), cursor.getString(USER_NAME_FIELD), cursor.getString(USER_RECEIVEPORT_FIELD), cursor.getString(MESSAGE_FIELD), cursor.getString(MESSAGE_SENDER_ADDRESS), cursor.getString(MESSAGE_SENDER_PORT), cursor.getString(MESSAGE_INDEX_FIELD)};
				db.commit();
				return res;
			}catch(SqlJetException e){
				System.out.println("An error in database has ocurred.");
				return null;
			}
		}
		
		public static void insertItem(InetAddress addr, String name, Integer recPort, String message, InetAddress senderAddr, Integer sendPort, Integer messageIndex) throws SqlJetException{
			db.beginTransaction(SqlJetTransactionMode.WRITE);
			ISqlJetTable table = db.getTable(TABLE_NAME);
			table.insert(addr.getHostAddress(), name, recPort.toString(), message, senderAddr.getHostAddress(), sendPort.toString(), messageIndex.toString());
			db.commit(); 
		}
		
		public static void clearUserData(InetAddress addr, String name){
			try{
				db.beginTransaction(SqlJetTransactionMode.READ_ONLY);;
				ISqlJetCursor cursor = db.getTable(TABLE_NAME).lookup(USER_INDEX, addr.getHostAddress(), name);
				while(!cursor.eof()){
					cursor.delete();
				}
				cursor.close();
			}catch(SqlJetException e){
				System.out.println("An error in database has ocurred when delete user record.");
			}
		}
		
		public static void clearUserData(InetAddress addr, Integer port){
			try{
				db.beginTransaction(SqlJetTransactionMode.READ_ONLY);;
				ISqlJetCursor cursor = db.getTable(TABLE_NAME).lookup(PORTUSER_INDEX, addr.getHostAddress(), port.toString());
				while(!cursor.eof()){
					cursor.delete();
				}
				cursor.close();
			}catch(SqlJetException e){
				System.out.println("An error in database has ocurred when delete user record.");
			}
		}
		
		public static HashMap<Integer, Request> retrieveDataByUser(InetAddress addr, Integer port) throws NumberFormatException, UnknownHostException{
			HashMap<Integer, Request> history = new HashMap<Integer, Request>();
			try{
				db.beginTransaction(SqlJetTransactionMode.READ_ONLY);;
				ISqlJetCursor cursor = db.getTable(TABLE_NAME).lookup(PORTUSER_INDEX, addr.getHostAddress(), port.toString());
				while(!cursor.eof()){
					Request req = new Request(cursor.getString(MESSAGE_INDEX_FIELD),InetAddress.getByName(cursor.getString(MESSAGE_SENDER_ADDRESS)), new Integer(cursor.getString(MESSAGE_SENDER_PORT)).intValue());
					history.put(new Integer(cursor.getString(MESSAGE_INDEX_FIELD)), req);
				}
				cursor.close();
			}catch(SqlJetException e){
				System.out.println("An error in database has ocurred when recovering user record.");
			}
			return history;
			
		}
		
		public static void clearDataBase(){
			File dbFile = new File(DB_NAME);
			dbFile.delete();
		}
		
		public static void closeConnect(){
			try {
				db.close();
			} catch (SqlJetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
	
}
