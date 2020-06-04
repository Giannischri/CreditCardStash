
import java.io.Serializable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author giann
 */
//serializable klash card me ta antistoixa pedia mias pistwtikhs
public class Card implements Serializable{
    private String type;
    private String number;
    private String username;
    private String expdate;
    private String verifnum;

    public Card(String type, String number,String username, String expdate, String verifnum) {
        this.type = type;
        this.number = number;
        this.username = username;
        this.expdate = expdate;
        this.verifnum = verifnum;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getExpdate() {
        return expdate;
    }

    public void setExpdate(String expdate) {
        this.expdate = expdate;
    }

    public String getVerifnum() {
        return verifnum;
    }

    public void setVerifnum(String verifnum) {
        this.verifnum = verifnum;
    }

    @Override
    public String toString() {
        return " " + type + "    " + number +  "    " + expdate + "    " + verifnum + "";
    }
    
}
