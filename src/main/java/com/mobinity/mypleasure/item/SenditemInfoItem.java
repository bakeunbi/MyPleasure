package com.mobinity.mypleasure.item;

import com.google.gson.annotations.SerializedName;

/**
 * 배송물품 정보를 저장하는 객체
 */
public class SenditemInfoItem {
   public int seq;
   @SerializedName("member_seq") public int memberSeq;
   public double weight;
   public int size;
   @SerializedName("time_length") public int timeLength;
   public String description;
   @SerializedName("reg_date") public String regDate;
   @SerializedName("distance_meter") public double distanceMeter;
   @SerializedName("image_filename") public String imageFilename;

   @Override
    public String toString() {
       return "SenditemInfoItem{" +
               "seq=" + seq +
               ", memberSeq=" + memberSeq + '\'' +
               ", weight='" + weight + '\'' +
               ", size='" + size + '\'' +
               ", timeLength='" + timeLength + '\'' +
               ", description='" + description + '\'' +
               ", regDate='" + regDate + '\'' +
               ", distance_meter='" + distanceMeter + '\'' +
               ", image_filename'" + imageFilename + '\'' +
               '}';
   }
}
