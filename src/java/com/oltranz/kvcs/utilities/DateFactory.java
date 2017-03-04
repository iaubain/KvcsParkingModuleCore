/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oltranz.kvcs.utilities;

import com.oltranz.kvcs.config.AppDesc;
import com.oltranz.kvcs.logic.CommandExec;
import static java.lang.System.out;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Hp
 */
public class DateFactory {
    public Date makeDate(String inputDate){
          try {
            out.print(AppDesc.APP_DESC+" Date factory receives date to convert: "+inputDate);
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = formatter.parse(inputDate.replaceAll("Z$", "+0000"));
            return date;
        } catch (ParseException ex) {
            Logger.getLogger(CommandExec.class.getName()).log(Level.SEVERE, null, ex);
            return new Date();
        }
      }
}
