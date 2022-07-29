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
	byte[] R;
	byte[] S;


	EcdsaResult(Object R_, Object S_) {
		R = Util.toBytes(R_);
		S = Util.toBytes(S_);

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


		return new EcdsaResult(Util.toBytesFromBigInteger(BR, 32), Util.toBytesFromBigInteger(BS, 32));

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



		vector.add(new ASN1Integer(Util.toBigInteger(this.R)));
		vector.add(new ASN1Integer(Util.toBigInteger(this.S)));


		outstrema.writeObject(new DERSequence(vector));
		outstrema.flush();

		return bOut.toByteArray();

	}
	public byte[] toRAWBYTES() throws IOException {

		return Util.joinBytes(R,S);

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

