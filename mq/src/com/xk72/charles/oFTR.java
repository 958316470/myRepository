//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xk72.charles;


import java.io.UnsupportedEncodingException;

public final class oFTR {
    private static String Yuaz;
    private static oFTR knIQ;
    private boolean lktV;
    private String ecCn;
    private int dnGU;
    private int RvLX;
    private static final int VZHJ = 1;
    private static final int iiPI = 2;
    private static final int ShaO = 3;
    private static final int SvIQ = 4;
    private static final long lIkx = 8800536498351690864L;
    private static final long wtWe = -5408575981733630035L;
    private static final long Lstl = -6517524745266237632L;
    private static final long hkQr = 5911726755176091652L;
    private static final String[] Zmaa;
    private static final int NlTB = 32;
    private static int pWfS;
    private static int Ysjo;
    private static int bEPq;
    private static int GNSw;
    private static int VOiG;
    private static int GnCV;
    private static int Awsv;
    private static int cqCI;
    private static int Plcd;
    private static int OSLI;
    private static int fpBu;
    private static int CuGv;
    private static int nNju;
    private static int sLwm;
    private static int ZJCr;
    private static int OEUQ;
    private static int ESwC;
    private static int aKQj;
    private static int zJTk;
    private static int dENq;
    private static int XtYC;
    private static int kxWh;
    private static int nPnr;
    private static int erqL;
    private static int GNEv;
    private static int DQQu;
    private int FsLy;
    private int AOuu;
    private int Osav;
    private int vLqw;
    private int DhVs;
    private int PRpo;
    private int xdtT;
    private int WUGl;
    private int uhAz;
    private int MINS;
    private int Kefv;
    private int hDRM;
    private int zXtT;
    private int izmg;
    private int DaOp;
    private int QEAR;
    private int wzDy;
    private int JcMe;
    private int XjDB;
    private int PWEi;
    private int fTZk;
    private int TokM;
    private int TGpk;
    private int hLVy;
    private int ZgYW;
    private int cHtK;
    private static final int fSAs = -1209970333;
    private static final int YMLA = -1640531527;

    public oFTR() {
        this.lktV = false;
        this.ecCn = "徐远东";
    }

    private oFTR(String var1, String var2) {
        this(var1, var2, 4);
    }

    private oFTR(String var1, String var2, int var3) {
        this.lktV = false;
        this.ecCn = "徐远东";

        try {
            byte var5 = 4;
            String var6 = var1.replaceAll("[  ᠎    　]", " ");
            long var9 = this.knIQ(var6, var2, var5);
            boolean var10000;
            if (this.Yuaz(var9)) {
                var10000 = true;
            } else if (var6.equals(var1)) {
                var10000 = false;
            } else {
                long var12 = this.knIQ(var1, var2, var5);
                var10000 = this.Yuaz(var12);
            }

            if (!var10000) {
                throw new RuntimeException(this.Yuaz(2));
            }
        } catch (NumberFormatException var14) {
            throw new RuntimeException(this.Yuaz(1));
        }

        this.ecCn = var1;
        this.lktV = true;
    }

    private static void Yuaz(oFTR var0) {
        knIQ = var0;
    }

    public static boolean Yuaz() {
      return true;
    }

    public static void knIQ() {
        knIQ = new oFTR();
    }

    public static String lktV() {
        oFTR var0 = knIQ;
        switch(var0.RvLX) {
            case 1:
                return var0.ecCn;
            case 2:
                return var0.ecCn + " - Site License";
            case 3:
                return var0.ecCn + " - Multi-Site License";
            default:
                return var0.ecCn;
        }
    }

    public static String Yuaz(String var0, String var1) {
        if (0 < 1){
            return null;
        }
        oFTR var3;
        try {
            var3 = new oFTR(var0, var1);
        } catch (RuntimeException var2) {
            return var2.getMessage();
        }

        knIQ = var3;
        return null;
    }

    private boolean ecCn() {
        return this.lktV;
    }

    private String dnGU() {
        switch(this.RvLX) {
            case 1:
                return this.ecCn;
            case 2:
                return this.ecCn + " - Site License";
            case 3:
                return this.ecCn + " - Multi-Site License";
            default:
                return this.ecCn;
        }
    }

    private int RvLX() {
        return this.dnGU;
    }

    private int VZHJ() {
        return this.RvLX;
    }

    private String Yuaz(int var1) {
        this.ecCn(8800536498351690864L);

        try {
            String var5;
            byte[] var2 = new byte[(var5 = Zmaa[var1]).length() / 2];

            for(int var3 = 0; var3 < var2.length; ++var3) {
                var2[var3] = (byte)Integer.parseInt(var5.substring(var3 << 1, (var3 << 1) + 2), 16);
            }

            byte[] var6;
            for(var1 = (var6 = this.lktV(var2)).length; var6[var1 - 1] == 0; --var1) {
                ;
            }

            return new String(var6, 0, var1, "UTF-8");
        } catch (UnsupportedEncodingException var4) {
            return "";
        }
    }

    private boolean Yuaz(String var1, String var2, int var3) {
        String var4 = var1.replaceAll("[  ᠎    　]", " ");
        long var5 = this.knIQ(var4, var2, var3);
        if (this.Yuaz(var5)) {
            return true;
        } else if (var4.equals(var1)) {
            return false;
        } else {
            long var8 = this.knIQ(var1, var2, var3);
            return this.Yuaz(var8);
        }
    }

    private static String Yuaz(String var0) {
        return var0.replaceAll("[  ᠎    　]", " ");
    }

    private boolean Yuaz(long var1) {
        int var3 = knIQ(var1);
        this.ecCn(var1);
        long var4 = var1;

        for(int var6 = 0; var6 < var3 + 35; ++var6) {
            var4 = this.lktV(var4);
        }

        return var4 == 5911726755176091652L;
    }

    private long knIQ(String var1, String var2, int var3) {
        if (var2.length() != 18) {
            throw new RuntimeException(this.Yuaz(0));
        } else if (!var2.equalsIgnoreCase("7055ce2f8cb4f9405f") && !var2.equalsIgnoreCase("5bae9d8cdea32760ae") && !var2.equalsIgnoreCase("f3264994d9ea6bc595") && !var2.equalsIgnoreCase("b9930cef009d3a7865") && !var2.equalsIgnoreCase("62bd6a5f95aa67998e") && !var2.equalsIgnoreCase("a1c536c35904e64584") && !var2.equalsIgnoreCase("d6e5590ecc05edd9b3") && !var2.equalsIgnoreCase("8fbe36ce2726458b18") && !var2.equalsIgnoreCase("042a8352caf1188945") && !var2.equalsIgnoreCase("9d26d5088770221c3c") && !var2.equalsIgnoreCase("e19b2a01905e4129bf") && !var2.equalsIgnoreCase("68ebe4c9d792f31057") && !var2.equalsIgnoreCase("4e4beb8a43e9feb9c7") && !var2.equalsIgnoreCase("d04d85b44b306fc9ec") && !var2.equalsIgnoreCase("2b5d21a38c9452e342") && !var2.equalsIgnoreCase("88cb89c26a813bce44") && !var2.equalsIgnoreCase("76c9ee78c8ab124054") && !var2.equalsIgnoreCase("729db7c98163ac7d3d") && !var2.equalsIgnoreCase("7c1d4761993c412472") && !var2.equalsIgnoreCase("08bc0b7ec91cd0f4aa") && !var2.equalsIgnoreCase("25bafae175decaedcc") && !var2.equalsIgnoreCase("3181aae6822ef90ccd") && !var2.equalsIgnoreCase("d7a8fe9dc9dc919f87") && !var2.equalsIgnoreCase("728dae81d9d22aca03") && !var2.equalsIgnoreCase("119a9b593348fa3e74") && !var2.equalsIgnoreCase("04ab87c8d69667878e") && !var2.equalsIgnoreCase("4b282d851ebd87a7bb") && !var2.equalsIgnoreCase("ed526255313b756e42") && !var2.equalsIgnoreCase("ed5ab211362ab25ca7") && !var2.equalsIgnoreCase("18f4789a3df48f3b15") && !var2.equalsIgnoreCase("67549e44b1c8d8d857") && !var2.equalsIgnoreCase("4593c6c54227c4f17d") && !var2.equalsIgnoreCase("1c59db29042e7df8ef") && !var2.equalsIgnoreCase("a647e3dd42ce9b409b") && !var2.equalsIgnoreCase("7e06d6a70b82858113") && !var2.equalsIgnoreCase("ef4b5a48595197a373") && !var2.equalsIgnoreCase("0ac55f6bebd0330640") && !var2.equalsIgnoreCase("1beda9831c78994f43") && !var2.equalsIgnoreCase("8a2b9debb15766bff9") && !var2.equalsIgnoreCase("da0e7561b10d974216") && !var2.equalsIgnoreCase("86257b04b8c303fd9a") && !var2.equalsIgnoreCase("a4036b2761c9583fda") && !var2.equalsIgnoreCase("18e69f6d5bc820d4d3") && !var2.equalsIgnoreCase("a13746cb3d1c83bca6") && !var2.equalsIgnoreCase("a4036b2761c9583fda")) {
            long var4 = Long.parseLong(var2.substring(2, 10), 16) << 32 | Long.parseLong(var2.substring(10, 18), 16);
            int var12 = Integer.parseInt(var2.substring(0, 2), 16);
            this.ecCn(-5408575981733630035L);
            long var7;
            if (knIQ(var7 = this.lktV(var4)) != var12) {
                throw new RuntimeException(this.Yuaz(1));
            } else {
                this.dnGU = (int)(var7 << 32 >>> 32 >>> 24);
                if (this.dnGU == 1) {
                    this.RvLX = 1;
                } else {
                    if (this.dnGU != var3) {
                        if (this.dnGU < var3) {
                            throw new RuntimeException(this.Yuaz(3));
                        }

                        throw new RuntimeException(this.Yuaz(1));
                    }

                    switch((int)(var7 << 32 >>> 32 >>> 16 & 255L)) {
                        case 1:
                            this.RvLX = 1;
                            break;
                        case 2:
                            this.RvLX = 2;
                            break;
                        case 3:
                            this.RvLX = 3;
                            break;
                        default:
                            throw new RuntimeException(this.Yuaz(1));
                    }
                }

                this.ecCn(8800536498351690864L);

                try {
                    byte[] var10 = var1.getBytes("UTF-8");
                    if ((var3 = (var12 = var10.length) + 4) % 8 != 0) {
                        var3 += 8 - var3 % 8;
                    }

                    byte[] var14 = new byte[var3];
                    System.arraycopy(var10, 0, var14, 4, var12);
                    var14[0] = (byte)(var12 >> 24);
                    var14[1] = (byte)(var12 >> 16);
                    var14[2] = (byte)(var12 >> 8);
                    var14[3] = (byte)var12;
                    byte[] var6 = this.lktV(var14);
                    int var11 = 0;
                    byte[] var13 = var6;
                    var3 = var6.length;

                    for(int var15 = 0; var15 < var3; ++var15) {
                        byte var5 = var13[var15];
                        var11 = (var11 ^= var5) << 3 | var11 >>> 29;
                    }

                    var11 ^= (int)(var7 >> 32);
                    return -6517524747541020672L | (long)var11 << 32 >>> 32;
                } catch (UnsupportedEncodingException var9) {
                    return -1L;
                }
            }
        } else {
            throw new RuntimeException(this.Yuaz(1));
        }
    }

    private static final long knIQ(String var0) {
        return Long.parseLong(var0.substring(2, 10), 16) << 32 | Long.parseLong(var0.substring(10, 18), 16);
    }

    private static final int lktV(String var0) {
        return Integer.parseInt(var0.substring(0, 2), 16);
    }

    private static final int Yuaz(byte[] var0) {
        int var1 = 0;
        int var2 = (var0 = var0).length;

        for(int var3 = 0; var3 < var2; ++var3) {
            byte var4 = var0[var3];
            var1 = (var1 ^= var4) << 3 | var1 >>> 29;
        }

        return var1;
    }

    private static final int knIQ(long var0) {
        long var2 = 0L;

        for(int var4 = 56; var4 >= 0; var4 -= 8) {
            var2 ^= var0 >>> var4 & 255L;
        }

        return Math.abs((int)(var2 & 255L));
    }

    private byte[] knIQ(byte[] var1) {
        int var2;
        int var3;
        if ((var3 = (var2 = var1.length) + 4) % 8 != 0) {
            var3 += 8 - var3 % 8;
        }

        byte[] var4 = new byte[var3];
        System.arraycopy(var1, 0, var4, 4, var2);
        var4[0] = (byte)(var2 >> 24);
        var4[1] = (byte)(var2 >> 16);
        var4[2] = (byte)(var2 >> 8);
        var4[3] = (byte)var2;
        return this.lktV(var4);
    }

    private byte[] lktV(byte[] var1) {
        byte[] var2 = new byte[var1.length];
        int var3 = var1.length;
        int var4 = 0;
        long var5 = 0L;

        for(int var7 = 0; var7 < var3; ++var7) {
            var5 = (var5 <<= 8) | (long)(var1[var7] & 255);
            ++var4;
            if (var4 == 8) {
                var5 = this.lktV(var5);
                var2[var7 - 7] = (byte)((int)(var5 >>> 56));
                var2[var7 - 6] = (byte)((int)(var5 >>> 48));
                var2[var7 - 5] = (byte)((int)(var5 >>> 40));
                var2[var7 - 4] = (byte)((int)(var5 >>> 32));
                var2[var7 - 3] = (byte)((int)(var5 >>> 24));
                var2[var7 - 2] = (byte)((int)(var5 >>> 16));
                var2[var7 - 1] = (byte)((int)(var5 >>> 8));
                var2[var7] = (byte)((int)var5);
                var4 = 0;
                var5 = 0L;
            }
        }

        return var2;
    }

    private long lktV(long var1) {
        int var3 = (int)var1 + this.FsLy;
        int var5 = (int)(var1 >>> 32) + this.AOuu;
        int var2 = var3 ^ var5;
        int var4 = var5 & 31;
        var3 = (var2 << var4 | var2 >>> 32 - var4) + this.Osav;
        var2 = var5 ^ var3;
        var4 = var3 & 31;
        var5 = (var2 << var4 | var2 >>> 32 - var4) + this.vLqw;
        var2 = var3 ^ var5;
        var4 = var5 & 31;
        var3 = (var2 << var4 | var2 >>> 32 - var4) + this.DhVs;
        var2 = var5 ^ var3;
        var4 = var3 & 31;
        var5 = (var2 << var4 | var2 >>> 32 - var4) + this.PRpo;
        var2 = var3 ^ var5;
        var4 = var5 & 31;
        var3 = (var2 << var4 | var2 >>> 32 - var4) + this.xdtT;
        var2 = var5 ^ var3;
        var4 = var3 & 31;
        var5 = (var2 << var4 | var2 >>> 32 - var4) + this.WUGl;
        var2 = var3 ^ var5;
        var4 = var5 & 31;
        var3 = (var2 << var4 | var2 >>> 32 - var4) + this.uhAz;
        var2 = var5 ^ var3;
        var4 = var3 & 31;
        var5 = (var2 << var4 | var2 >>> 32 - var4) + this.MINS;
        var2 = var3 ^ var5;
        var4 = var5 & 31;
        var3 = (var2 << var4 | var2 >>> 32 - var4) + this.Kefv;
        var2 = var5 ^ var3;
        var4 = var3 & 31;
        var5 = (var2 << var4 | var2 >>> 32 - var4) + this.hDRM;
        var2 = var3 ^ var5;
        var4 = var5 & 31;
        var3 = (var2 << var4 | var2 >>> 32 - var4) + this.zXtT;
        var2 = var5 ^ var3;
        var4 = var3 & 31;
        var5 = (var2 << var4 | var2 >>> 32 - var4) + this.izmg;
        var2 = var3 ^ var5;
        var4 = var5 & 31;
        var3 = (var2 << var4 | var2 >>> 32 - var4) + this.DaOp;
        var2 = var5 ^ var3;
        var4 = var3 & 31;
        var5 = (var2 << var4 | var2 >>> 32 - var4) + this.QEAR;
        var2 = var3 ^ var5;
        var4 = var5 & 31;
        var3 = (var2 << var4 | var2 >>> 32 - var4) + this.wzDy;
        var2 = var5 ^ var3;
        var4 = var3 & 31;
        var5 = (var2 << var4 | var2 >>> 32 - var4) + this.JcMe;
        var2 = var3 ^ var5;
        var4 = var5 & 31;
        var3 = (var2 << var4 | var2 >>> 32 - var4) + this.XjDB;
        var2 = var5 ^ var3;
        var4 = var3 & 31;
        var5 = (var2 << var4 | var2 >>> 32 - var4) + this.PWEi;
        var2 = var3 ^ var5;
        var4 = var5 & 31;
        var3 = (var2 << var4 | var2 >>> 32 - var4) + this.fTZk;
        var2 = var5 ^ var3;
        var4 = var3 & 31;
        var5 = (var2 << var4 | var2 >>> 32 - var4) + this.TokM;
        var2 = var3 ^ var5;
        var4 = var5 & 31;
        var3 = (var2 << var4 | var2 >>> 32 - var4) + this.TGpk;
        var2 = var5 ^ var3;
        var4 = var3 & 31;
        var5 = (var2 << var4 | var2 >>> 32 - var4) + this.hLVy;
        var2 = var3 ^ var5;
        var4 = var5 & 31;
        var3 = (var2 << var4 | var2 >>> 32 - var4) + this.ZgYW;
        var2 = var5 ^ var3;
        var4 = var3 & 31;
        return ((long)((var2 << var4 | var2 >>> 32 - var4) + this.cHtK) << 32) + ((long)var3 & 4294967295L);
    }

    private void ecCn(long var1) {
        int var3 = (int)var1;
        int var4 = (int)(var1 >>> 32);
        int var5 = pWfS;
        int var6 = this.FsLy = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6;
        int var2 = var6 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = Ysjo + var6 + var2;
        var6 = this.AOuu = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = bEPq + var6 + var2;
        var6 = this.Osav = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = GNSw + var6 + var2;
        var6 = this.vLqw = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = VOiG + var6 + var2;
        var6 = this.DhVs = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = GnCV + var6 + var2;
        var6 = this.PRpo = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = Awsv + var6 + var2;
        var6 = this.xdtT = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = cqCI + var6 + var2;
        var6 = this.WUGl = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = Plcd + var6 + var2;
        var6 = this.uhAz = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = OSLI + var6 + var2;
        var6 = this.MINS = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = fpBu + var6 + var2;
        var6 = this.Kefv = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = CuGv + var6 + var2;
        var6 = this.hDRM = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = nNju + var6 + var2;
        var6 = this.zXtT = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = sLwm + var6 + var2;
        var6 = this.izmg = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = ZJCr + var6 + var2;
        var6 = this.DaOp = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = OEUQ + var6 + var2;
        var6 = this.QEAR = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = ESwC + var6 + var2;
        var6 = this.wzDy = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = aKQj + var6 + var2;
        var6 = this.JcMe = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = zJTk + var6 + var2;
        var6 = this.XjDB = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = dENq + var6 + var2;
        var6 = this.PWEi = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = XtYC + var6 + var2;
        var6 = this.fTZk = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = kxWh + var6 + var2;
        var6 = this.TokM = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = nPnr + var6 + var2;
        var6 = this.TGpk = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = erqL + var6 + var2;
        var6 = this.hLVy = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = GNEv + var6 + var2;
        var6 = this.ZgYW = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = DQQu + var6 + var2;
        var6 = this.cHtK = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.FsLy + var6 + var2;
        var6 = this.FsLy = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.AOuu + var6 + var2;
        var6 = this.AOuu = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.Osav + var6 + var2;
        var6 = this.Osav = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.vLqw + var6 + var2;
        var6 = this.vLqw = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.DhVs + var6 + var2;
        var6 = this.DhVs = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.PRpo + var6 + var2;
        var6 = this.PRpo = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.xdtT + var6 + var2;
        var6 = this.xdtT = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.WUGl + var6 + var2;
        var6 = this.WUGl = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.uhAz + var6 + var2;
        var6 = this.uhAz = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.MINS + var6 + var2;
        var6 = this.MINS = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.Kefv + var6 + var2;
        var6 = this.Kefv = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.hDRM + var6 + var2;
        var6 = this.hDRM = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.zXtT + var6 + var2;
        var6 = this.zXtT = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.izmg + var6 + var2;
        var6 = this.izmg = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.DaOp + var6 + var2;
        var6 = this.DaOp = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.QEAR + var6 + var2;
        var6 = this.QEAR = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.wzDy + var6 + var2;
        var6 = this.wzDy = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.JcMe + var6 + var2;
        var6 = this.JcMe = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.XjDB + var6 + var2;
        var6 = this.XjDB = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.PWEi + var6 + var2;
        var6 = this.PWEi = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.fTZk + var6 + var2;
        var6 = this.fTZk = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.TokM + var6 + var2;
        var6 = this.TokM = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.TGpk + var6 + var2;
        var6 = this.TGpk = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.hLVy + var6 + var2;
        var6 = this.hLVy = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.ZgYW + var6 + var2;
        var6 = this.ZgYW = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.cHtK + var6 + var2;
        var6 = this.cHtK = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.FsLy + var6 + var2;
        var6 = this.FsLy = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.AOuu + var6 + var2;
        var6 = this.AOuu = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.Osav + var6 + var2;
        var6 = this.Osav = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.vLqw + var6 + var2;
        var6 = this.vLqw = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.DhVs + var6 + var2;
        var6 = this.DhVs = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.PRpo + var6 + var2;
        var6 = this.PRpo = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.xdtT + var6 + var2;
        var6 = this.xdtT = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.WUGl + var6 + var2;
        var6 = this.WUGl = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.uhAz + var6 + var2;
        var6 = this.uhAz = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.MINS + var6 + var2;
        var6 = this.MINS = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.Kefv + var6 + var2;
        var6 = this.Kefv = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.hDRM + var6 + var2;
        var6 = this.hDRM = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.zXtT + var6 + var2;
        var6 = this.zXtT = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.izmg + var6 + var2;
        var6 = this.izmg = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.DaOp + var6 + var2;
        var6 = this.DaOp = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.QEAR + var6 + var2;
        var6 = this.QEAR = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.wzDy + var6 + var2;
        var6 = this.wzDy = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.JcMe + var6 + var2;
        var6 = this.JcMe = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.XjDB + var6 + var2;
        var6 = this.XjDB = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.PWEi + var6 + var2;
        var6 = this.PWEi = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.fTZk + var6 + var2;
        var6 = this.fTZk = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.TokM + var6 + var2;
        var6 = this.TokM = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var4 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.TGpk + var6 + var2;
        var6 = this.TGpk = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var3 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.hLVy + var6 + var2;
        var6 = this.hLVy = var5 << 3 | var5 >>> 29;
        var5 = var4 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.ZgYW + var6 + var2;
        var6 = this.ZgYW = var5 << 3 | var5 >>> 29;
        var5 = var3 + var6 + var2;
        var2 = var6 + var2 & 31;
        var2 = var5 << var2 | var5 >>> 32 - var2;
        var5 = this.cHtK + var6 + var2;
        this.cHtK = var5 << 3 | var5 >>> 29;
    }

    static {
        knIQ();
        Zmaa = new String[]{"b241993e8a12c782348e4652cc22c2501d9d6f248e91a3d849275666a0ff7d954fdf638f0d03098c52c4710a5e619b9b09cd6cd027ea3bdb937172b3fdf0bded3d684333798880bb78780f6f6644580409ac882bc021732a", "b241993e8a12c782348e4652cc22c250c30afb881b44ba4dd936c44a573755b5276046dc3ae32e58d10f467421f51ca607b0e29f53cd8f38dd9eee548398b195348e4652cc22c2502feb5f8fd884cb3c9a330ee10954d071", "8a24264c4ae5e5371d663158ccbd75e5a5d70bef5d61291ba3af58b92fe98f7a5c8f83abe09b0c1b3f469d5ad85a3a01e81a2248a290b22d05f52db22eb8b10af10437ddcf2f437b1a519b09a9c4f2c374a882757515e2e2fde238a4eccc62d3fc36d9a77dcbd7cc05236b02716005836b21e58a07330bb18136139263e71a0f79382179978b680a", "96122782ec21d0584881fa8dc6b2ff60585bcafbaeec4bd03874fc7ce730dcb3515b9fb963790219047bf20363167e9967cc1b0851ae39b63d831e55c196a04d7ae5be1b671bedc43b7e8ca175e6d3af2610c3f6b5863d0000ccc9ff3b971946974d3cb7be340cb08475020696df69ac20764f7709cb63e3aac239578db58e85"};
        pWfS = -1209970333;
        DQQu = (GNEv = (erqL = (nPnr = (kxWh = (XtYC = (dENq = (zJTk = (aKQj = (ESwC = (OEUQ = (ZJCr = (sLwm = (nNju = (CuGv = (fpBu = (OSLI = (Plcd = (cqCI = (Awsv = (GnCV = (VOiG = (GNSw = (bEPq = (Ysjo = -1209970333 + -1640531527) + -1640531527) + -1640531527) + -1640531527) + -1640531527) + -1640531527) + -1640531527) + -1640531527) + -1640531527) + -1640531527) + -1640531527) + -1640531527) + -1640531527) + -1640531527) + -1640531527) + -1640531527) + -1640531527) + -1640531527) + -1640531527) + -1640531527) + -1640531527) + -1640531527) + -1640531527) + -1640531527) + -1640531527;
    }
}
