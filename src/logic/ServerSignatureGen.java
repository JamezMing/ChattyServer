package logic;


import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.interfaces.*;

import javax.crypto.KeyAgreement;
import javax.crypto.ShortBufferException;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.*;
import javax.xml.bind.DatatypeConverter;


public class ServerSignatureGen{
	DHParameterSpec dhParamSpec;
	KeyPair myKeyPair; 
	KeyAgreement myAgreement; 
	byte[] mySecret;
	boolean isInit = false;
	
	
	
	public ServerSignatureGen(){}
	
	public void init(byte[] serverPubKey) throws NoSuchAlgorithmException, InvalidParameterSpecException, InvalidAlgorithmParameterException, InvalidKeyException, InvalidKeySpecException, IllegalStateException, ShortBufferException{
		KeyFactory cliKeyFac = KeyFactory.getInstance("DH");
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(serverPubKey);
		KeyPairGenerator myKeypairGen = KeyPairGenerator.getInstance("DH");
		PublicKey serPubKey = cliKeyFac.generatePublic(x509KeySpec);
		DHParameterSpec serverParamSpec = ((DHPublicKey)serPubKey).getParams();
		myKeypairGen.initialize(serverParamSpec);
		myKeyPair = myKeypairGen.generateKeyPair();
		myAgreement = KeyAgreement.getInstance("DH");
		myAgreement.init(myKeyPair.getPrivate());
		isInit = true;
		myAgreement.doPhase(serPubKey, true);
		mySecret = myAgreement.generateSecret();
	}

	
	public int getLen(){
		return mySecret.length;
	}
	
	public String getSecretString(){
		return DatatypeConverter.printHexBinary(mySecret);
	}
	
	public byte[] getSecret(){
		return mySecret; 
	}
	
	
	public byte[] getPublicKey(){
		if(isInit == false){
			System.out.println("Generator has not been initiated yet.");
			return null;
		}
		return myKeyPair.getPublic().getEncoded();
	}
	
	public byte[] getPrivateKey(){
		if(isInit = false){
			System.out.println("Generator has not been initiated yet.");
			return null;
		}
		return myKeyPair.getPrivate().getEncoded();
	}

}
