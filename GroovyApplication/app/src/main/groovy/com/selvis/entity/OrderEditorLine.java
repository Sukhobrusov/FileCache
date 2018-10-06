package com.selvis.entity;


import java.math.BigDecimal;
import java.util.List;

public class OrderEditorLine {

    public String skuId;

    Supplier supplier;

    public String productId;

    String barcode;

    Boolean isNewProduct;

    public String wareName;

    String group1;

    String group2;

    String group3;

    String brand;

    BigDecimal price;

    BigDecimal blackPrice;

    BigDecimal prevPrice;

    List<Multiplicity> multiples;

    Integer quantity;

    Integer stock;

    Boolean haveImage;

    String abc;

    BigDecimal weight;

    String weightMeasure;

    List<BonusProgram> bonusPrograms;

    public static class BonusProgram {

        String bonusIcon;

        ClassifiedBonus classifiedBonus;

        String description;

        String guid;

    }

    public static class ClassifiedBonus {

        BigDecimal actualPrice;

        String bonusType;

        String bpClass;

        String description;

        BigDecimal minDiscount;

    }


    @Override
    public String toString() {
        return "OrderEditorLine{" +
                "skuId='" + skuId + '\'' +
                ", supplier=" + supplier +
                ", productId='" + productId + '\'' +
                ", barcode='" + barcode + '\'' +
                ", isNewProduct=" + isNewProduct +
                ", wareName='" + wareName + '\'' +
                ", group1='" + group1 + '\'' +
                ", group2='" + group2 + '\'' +
                ", group3='" + group3 + '\'' +
                ", brand='" + brand + '\'' +
                ", price=" + price +
                ", blackPrice=" + blackPrice +
                ", prevPrice=" + prevPrice +
                ", multiples=" + multiples +
                ", quantity=" + quantity +
                ", stock=" + stock +
                ", haveImage=" + haveImage +
                ", abc='" + abc + '\'' +
                ", weight=" + weight +
                ", weightMeasure='" + weightMeasure + '\'' +
                '}';
    }
}
