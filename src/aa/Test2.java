package aa;

import QuantIF_Project.utils.DicomUtils;




/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Cyriac
 */
public class Test2 {
    public static void main(String[] args) {
        
      String time1 = "082804.406005";
      String time2 = "083010.406005";
      
      System.out.println(DicomUtils.getMinutesBetweenDicomDates(time1, time2));
    }
    
}
