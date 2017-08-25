package com.leauto.link.lightcar.module;

import com.leauto.link.lightcar.LogUtils;

/**
 * Created by Jerome on 2017/5/17.
 */

public class AVNInfo {
    private String vin;
    private String sn;
    private String partnum;
    private String hwver;
    private String swver;
    private String mode;

    public AVNInfo() {
    }

    public AVNInfo(String vin, String sn, String partnum, String hwver, String swver, String mode) {
        this.vin = vin;
        this.sn = sn;
        this.partnum = partnum;
        this.hwver = hwver;
        this.swver = swver;
        this.mode = mode;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getPartnum() {
        return partnum;
    }

    public void setPartnum(String partnum) {
        this.partnum = partnum;
    }

    public String getHwver() {
        return hwver;
    }

    public void setHwver(String hwver) {
        this.hwver = hwver;
    }

    public String getSwver() {
        return swver;
    }

    public void setSwver(String swver) {
        this.swver = swver;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("vin="+vin);
        sb.append(";sn="+sn);
        sb.append(";partnum="+partnum);
        sb.append(";hwver="+hwver);
        sb.append(";swver="+swver);
        sb.append(";mode="+mode);
        return sb.toString();
    }
}
