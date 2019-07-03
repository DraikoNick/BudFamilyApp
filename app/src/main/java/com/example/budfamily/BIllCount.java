package com.example.budfamily;

public class BIllCount {
    private int BillID;
    private String BillName;
    private int BillCurrency;
    private Double BillSumm;
    BIllCount nextBill;

    public void setBillID(int billID) {
        BillID = billID;
    }
    public int getBillID() {
        return BillID;
    }
    public String getBillName() {
        return BillName;
    }
    public void setBillName(String billName) {
        BillName = billName;
    }
    public Double getBillSumm() {
        return BillSumm;
    }
    public void setBillSumm(Double billSumm) {
        BillSumm = billSumm;
    }
    public void setBillCurrency(int billCurrency) {
        BillCurrency = billCurrency;
    }
    public int getBillCurrency() {
        return BillCurrency;
    }

    BIllCount(){
        setBillName("");
        setBillCurrency(0);
        setBillSumm(0.0);
        setBillID(0);
    }
    BIllCount(int id, String name, int curr, Double summ){
        setBillID(id);
        setBillName(name);
        setBillCurrency(curr);
        setBillSumm(summ);
    }

    public void addMoney(Double addM){
        setBillSumm(getBillSumm()+addM);
    }
    public boolean takeMoney(Double takeM){
        if(getBillSumm()<takeM){
            return false;
        }else{
            setBillSumm(getBillSumm()-takeM);
            return true;
        }
    }

    public void addNextBill(int id, String name, int curr, Double summ){
        nextBill.setBillID(id);
        nextBill.setBillName(name);
        nextBill.setBillCurrency(curr);
        nextBill.setBillSumm(summ);
    }
}//end BillCount

