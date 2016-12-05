package global;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.bind.DatatypeConverter;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

public final class ServerUserDataBaseManager {
	private static SqlJetDb db;
	public static final String DB_NAME = "ServerUserData.db";
	private static final String TABLE_NAME = "ServerUser";
	private static final String USER_ADDRESS_FIELD = "user_address";
	private static final String USER_NAME_FIELD = "user_name";
	private static final String USER_RECEIVEPORT_FIELD = "user_receive_port";
	private static final String USER_AVALIABLITY_FIELD = "user_avaliability";
	private static final String ALLOWED_USER_FIELD = "allowed_user";//Allowed user is splitted by character [A, B, C ...]
	private static final String USER_KEY_FIELD = "user_key";
	private static final String ADDRESSPORT_INDEX = "addressport_index";
	private static final String ADDRESSNAME_INDEX = "addressname_index";

	private static final String KEY_INDEX = "key_index";
	
	private ServerUserDataBaseManager(){}
	
	public static void init(){
		File dbFile = new File(DB_NAME);
		System.out.println(dbFile.exists());
		if(dbFile.exists()){
			try {
				System.out.println("User database found. ");
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
	            		" (" + USER_ADDRESS_FIELD + " TEXT NOT NULL, "+ USER_NAME_FIELD + " TEXT NOT NULL , " +  USER_RECEIVEPORT_FIELD + " TEXT NOT NULL , " + USER_AVALIABLITY_FIELD + " TEXT NOT NULL, " + 
	            		USER_KEY_FIELD + " TEXT NOT NULL PRIMARY KEY, " + ALLOWED_USER_FIELD + " TEXT NOT NULL "  + ")";
	            String createNameQuery = "CREATE UNIQUE INDEX " + ADDRESSPORT_INDEX + " ON " + TABLE_NAME + "(" +  USER_ADDRESS_FIELD + ", " + USER_NAME_FIELD  + ")"; 
	            String createPortQuery = "CREATE UNIQUE INDEX " + ADDRESSNAME_INDEX + " ON " + TABLE_NAME + "(" +  USER_ADDRESS_FIELD + ", " + USER_RECEIVEPORT_FIELD  + ")"; 
				db.createTable(createTableQuery);
				db.createIndex(createNameQuery);
				db.createIndex(createPortQuery);

				db.commit();
			} catch (SqlJetException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}}
		
	}

	
	public static void insertItem(InetAddress addr, String userName, Integer recPort, String isAvaliable, byte[] userKey, ArrayList<String> allowedListUser) throws SqlJetException{
		String address = addr.getHostAddress();
		String port = recPort.toString();
		db.beginTransaction(SqlJetTransactionMode.WRITE);
		ISqlJetTable table = db.getTable(TABLE_NAME);
		System.out.println("New user entry logged. ");
		table.insert(address, userName, port, isAvaliable, DatatypeConverter.printHexBinary(userKey), allowedListUser.toString());
		db.commit();
	}
	
	public static void modifyUserStatus(User u, int status) throws SqlJetException{
		db.beginTransaction(SqlJetTransactionMode.WRITE);
		ISqlJetTable table = db.getTable(TABLE_NAME);
		ISqlJetCursor cursor = table.lookup(ADDRESSPORT_INDEX, u.getAddr().getHostAddress(), String.valueOf(u.getRecevingPort()));
		while(!cursor.eof()){
			long index = cursor.getRowIndex();
			Object[] newVal = cursor.getRowValues();
			newVal[3] = String.valueOf(status);
			cursor.updateWithRowId(index, newVal);
		}
	}
	
	
	public static void modifyUserStatus(InetAddress addr, Integer port, int status) throws SqlJetException{
		db.beginTransaction(SqlJetTransactionMode.WRITE);
		ISqlJetTable table = db.getTable(TABLE_NAME);
		ISqlJetCursor cursor = table.lookup(ADDRESSPORT_INDEX, addr.getHostAddress(), port.toString());
		while(!cursor.eof()){
			long index = cursor.getRowIndex();
			Object[] newVal = cursor.getRowValues();
			newVal[3] = String.valueOf(status);
			cursor.updateWithRowId(index, newVal);
		}
	}
	
	
	public static void modifyUserStatus(InetAddress addr, String name, int status) throws SqlJetException{
		db.beginTransaction(SqlJetTransactionMode.WRITE);
		ISqlJetTable table = db.getTable(TABLE_NAME);
		ISqlJetCursor cursor = table.lookup(ADDRESSNAME_INDEX, addr.getHostAddress(), name);
		while(!cursor.eof()){
			long index = cursor.getRowIndex();
			Object[] newVal = cursor.getRowValues();
			newVal[3] = String.valueOf(status);
			cursor.updateWithRowId(index, newVal);
		}
	}
	
	
	public static CopyOnWriteArrayList<User> recoverUserData() throws UnknownHostException, SqlJetException{
		db.beginTransaction(SqlJetTransactionMode.WRITE);
		CopyOnWriteArrayList<User> userList = new CopyOnWriteArrayList<User>();
		try {
			ISqlJetCursor cursor = db.getTable(TABLE_NAME).open();
			while(!(cursor.eof())){
				InetAddress addr = InetAddress.getByName(cursor.getString(USER_ADDRESS_FIELD));
				Integer port = new Integer(cursor.getString(USER_RECEIVEPORT_FIELD));
				String name = cursor.getString(USER_NAME_FIELD);
				String avaliablity = cursor.getString(USER_AVALIABLITY_FIELD);
				Integer ava = new Integer(avaliablity);
				byte[] userKey = javax.xml.bind.DatatypeConverter.parseHexBinary(cursor.getString(USER_KEY_FIELD));
				String rawList = cursor.getString(ALLOWED_USER_FIELD);
				String[] uList = rawList.trim().substring(1, rawList.length()-1).split(",");
				User u = new User(name, addr, port, ava, userKey, new ArrayList<String>(Arrays.asList(uList)));
				userList.add(u);
			}
		} catch (SqlJetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userList;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


}
