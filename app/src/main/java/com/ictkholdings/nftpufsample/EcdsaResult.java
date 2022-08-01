package com.ictkholdings.nftpufsample;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.Arrays;


import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.DERNumericString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.ASN1Integer;


public class EcdsaResult {
//	byte[] R;
//	byte[] S;
	BigInteger m_BR;
	BigInteger m_BS;

	EcdsaResult(Object R_, Object S_) {
		m_BR = Util.toBigInteger(Util.toBytes(R_));
		m_BS = Util.toBigInteger(Util.toBytes(S_));

	}
	EcdsaResult(BigInteger R_, BigInteger S_) throws IOException {
		m_BR = R_;
		m_BS = S_;
//		R = Util.toBytesFromBigInteger(R_,32);
//		S = Util.toBytesFromBigInteger(S_,32);

	}

	public static EcdsaResult FromDER(Object asnvalue_)
			throws IOException {
		ASN1InputStream asn1 = new ASN1InputStream(
				new ByteArrayInputStream(Util.toBytes(asnvalue_)));
		DLSequence obj = (DLSequence) asn1.readObject();
		// System.out.println(ASN1Dump.dumpAsString(obj));

		// DLSequence seq = (DLSequence) asn1.readObject();

		BigInteger BR = ((ASN1Integer) obj.getObjectAt(0)).getValue();
		BigInteger BS = ((ASN1Integer) obj.getObjectAt(1)).getValue();

		return new EcdsaResult(BR,BS);
		//return new EcdsaResult(Util.toBytesFromBigInteger(BR, 32), Util.toBytesFromBigInteger(BS, 32));

	}
	public static EcdsaResult FromRAWBYTES(Object rawbytes)
			throws IOException {

		byte [] r_ = Util.subBytes(Util.toBytes(rawbytes),0,32);
		byte [] s_ = Util.subBytes(Util.toBytes(rawbytes),32,64);

		return new EcdsaResult(r_,s_);

	}


	public byte[] toDER() throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();

		ASN1OutputStream outstrema = ASN1OutputStream.create(bOut);

		ASN1EncodableVector vector = new ASN1EncodableVector();



		vector.add(new ASN1Integer(this.m_BR));
		vector.add(new ASN1Integer(this.m_BS));


		outstrema.writeObject(new DERSequence(vector));
		outstrema.flush();

		return bOut.toByteArray();

	}
	public byte[] toRAWBYTES() throws IOException {
		//		R = Util.toBytesFromBigInteger(R_,32);
//		S = Util.toBytesFromBigInteger(S_,32);


		return Util.joinBytes(
				Util.toBytesFromBigInteger(this.m_BR,32),
				Util.toBytesFromBigInteger(this.m_BS,32));

	}
	public BigInteger getR(){

		return m_BR;
	}
	public BigInteger getS(){

		return m_BS;
	}
	public static void main(String[] args) throws Exception {

		EcdsaResult inst = new EcdsaResult(
				"B3782FB0EBF16CBFC4EB2B85DF35531898C3468625425A5A946ED8053D41B517",
				"E1BC5E9991FEA45DD6B1AFF9B817754302402651522723350A5DCCC708486BA9");

		inst = EcdsaResult.FromRAWBYTES("B3782FB0EBF16CBFC4EB2B85DF35531898C3468625425A5A946ED8053D41B517E1BC5E9991FEA45DD6B1AFF9B817754302402651522723350A5DCCC708486BA9");

		Util.printHexstr("DER", inst.toDER());

		inst = EcdsaResult.FromDER("3046022100B3782FB0EBF16CBFC4EB2B85DF35531898C3468625425A5A946ED8053D41B517022100E1BC5E9991FEA45DD6B1AFF9B817754302402651522723350A5DCCC708486BA9");
		Util.printHexstr("RAW BYTES", inst.toRAWBYTES());



	}
}

