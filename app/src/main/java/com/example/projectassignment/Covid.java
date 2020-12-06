package com.example.projectassignment;

public class Covid {
    private String country;
    private String province;
    private String caseNumber;
    private String startDate;
    private long id;
    private String countryCode;

    /**
     * constructor
     * @param id
     * @param countryCode
     * @param country
     * @param province
     * @param caseNumber
     * @param startDate
     */
    public Covid(long id,String countryCode,String country,String province,String caseNumber,String startDate){
        this.id = id;
        this.countryCode = countryCode;
        this.country = country;
        this.province = province;
        this.caseNumber = caseNumber;
        this.startDate = startDate;

    }
    public Covid(String countryCode,String country,String province,String caseNumber,String startDate){
        this.countryCode = countryCode;
        this.country = country;
        this.province = province;
        this.caseNumber = caseNumber;
        this.startDate = startDate;
    }

    public String getCountry(){
        return country;
    }

    public long getId(){
        return id;
    }

    public String getProvince(){
        return province;
    }

    public String getCaseNumber(){
        return caseNumber;
    }

    public String getStartDate(){
        return startDate;
    }

    public String getCountryCode(){return countryCode;}

}
