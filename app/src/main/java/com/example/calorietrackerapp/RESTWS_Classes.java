package com.example.calorietrackerapp;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.Update;
import android.content.Context;

import java.io.Serializable;
import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

public class RESTWS_Classes {

    RESTWS_Classes() {
    }

}

class ConsumtionTable {
    private Integer consumptionid;
    private Integer userid;
    private String date;
    private Integer foodid;
    private Integer dailyquantity;

    ConsumtionTable(Integer consid, Integer conuserid, String condate, Integer confoodid, Integer condailyquantity) {
        this.consumptionid = consid;
        this.userid = conuserid;
        this.date = condate;
        this.foodid = confoodid;
        this.dailyquantity = condailyquantity;
    }

    ConsumtionTable() {
    }

    public Integer getConuserid() {
        return userid;
    }

    public void setConuserid(Integer conuserid) {
        this.userid = conuserid;
    }

    public Integer getConsumptionid() {
        return consumptionid;
    }

    public void setConsumptionid(Integer consumptionid) {
        this.consumptionid = consumptionid;
    }

    public String getCondate() {
        return date;
    }

    public void setCondate(String condate) {
        this.date = condate;
    }

    public Integer getConfoodid() {
        return foodid;
    }

    public void setConfoodid(Integer confoodid) {
        this.foodid = confoodid;
    }

    public Integer getDailyquantity() {
        return dailyquantity;
    }

    public void setDailyquantity(Integer dailyquantity) {
        this.dailyquantity = dailyquantity;
    }
}

class CredentialTable {
    private Integer credentialid;
    private Integer userid;
    private String username;
    private String passwordhash;
    private String signupdate;

    CredentialTable(Integer credid, Integer creduserid, String credusername, String credpasswordhash, String credsignupdate) {
        this.credentialid = credid;
        this.userid = creduserid;
        this.username = credusername;
        this.passwordhash = credpasswordhash;
        this.signupdate = credsignupdate;
    }

    CredentialTable() {
    }

    public Integer getCredid() {
        return credentialid;
    }

    public void setCredid(Integer credid) {
        this.credentialid = credid;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCredpasswordhash() {
        return passwordhash;
    }

    public void setCredpasswordhash(String credpasswordhash) {
        this.passwordhash = credpasswordhash;
    }

    public String getSignupdate() {
        return signupdate;
    }

    public void setSignupdate(String signupdate) {
        this.signupdate = signupdate;
    }
}

class FoodTable {
    private Integer foodid;
    private String name;
    private String category;
    private Integer calorieamount;
    private String servingunit;
    private Integer servingamount;
    private Integer fat;

    FoodTable(Integer foodid, String foodname, String foodcat, Integer foodcalamount, String foodservingunit, Integer foodservingamount, Integer foodfat) {
        this.foodid = foodid;
        this.name = foodname;
        this.category = foodcat;
        this.calorieamount = foodcalamount;
        this.servingunit = foodservingunit;
        this.servingamount = foodservingamount;
        this.fat = foodfat;
    }

    FoodTable() {
    }

    public Integer getFoodid() {
        return foodid;
    }

    public void setFoodid(Integer foodid) {
        this.foodid = foodid;
    }

    public String getFoodname() {
        return name;
    }

    public void setFoodname(String foodname) {
        this.name = foodname;
    }

    public String getFoodcat() {
        return category;
    }

    public void setFoodcat(String foodcat) {
        this.category = foodcat;
    }

    public Integer getFoodcalamount() {
        return calorieamount;
    }

    public void setFoodcalamount(Integer foodcalamount) {
        this.calorieamount = foodcalamount;
    }

    public String getFoodservingunit() {
        return servingunit;
    }

    public void setFoodservingunit(String foodservingunit) {
        this.servingunit = foodservingunit;
    }

    public Integer getFoodservingamount() {
        return servingamount;
    }

    public void setFoodservingamount(Integer foodservingamount) {
        this.servingamount = foodservingamount;
    }

    public Integer getFat() {
        return fat;
    }

    public void setFat(Integer fat) {
        this.fat = fat;
    }
}

@Entity
class ReportTable implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private Integer reportid;
    @ColumnInfo(name = "userid")
    private Integer userid;
    @ColumnInfo(name = "date")
    private String date;
    @ColumnInfo(name = "totalcaloriesconsumed")
    private Integer totalcaloriesconsumed;
    @ColumnInfo(name = "totalstepstaken")
    private Integer totalstepstaken;
    @ColumnInfo(name = "dailycaloriegoal")
    private Integer dailycaloriegoal;
    @ColumnInfo(name = "totalcaloriesburned")
    private Integer totalcaloriesburned;


    ReportTable(Integer reportid, Integer reportuserid, String reportdate, Integer reporttotalcaloriesconsumed, Integer reporttotalstepstaken, Integer reportdailycalgoal, Integer tcb) {
        this.reportid = reportid;
        this.userid = reportuserid;
        this.date = reportdate;
        this.totalcaloriesconsumed = reporttotalcaloriesconsumed;
        this.totalstepstaken = reporttotalstepstaken;
        this.dailycaloriegoal = reportdailycalgoal;
        this.totalcaloriesburned = tcb;
    }

    ReportTable() {
    }

    public Integer getReportid() {
        return reportid;
    }

    public void setReportid(Integer reportid) {
        this.reportid = reportid;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer reportuserid) {
        this.userid = reportuserid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String reportdate) {
        this.date = reportdate;
    }

    public Integer getTotalcaloriesconsumed() {
        return totalcaloriesconsumed;
    }

    public void setTotalcaloriesconsumed(Integer reporttotalcaloriesconsumed) {
        this.totalcaloriesconsumed = reporttotalcaloriesconsumed;
    }

    public Integer getTotalstepstaken() {
        return totalstepstaken;
    }

    public void setTotalstepstaken(Integer reporttotalstepstaken) {
        this.totalstepstaken = reporttotalstepstaken;
    }

    public Integer getDailycaloriegoal() {
        return dailycaloriegoal;
    }

    public void setDailycaloriegoal(Integer reportdailycalgoal) {
        this.dailycaloriegoal = reportdailycalgoal;
    }

    public Integer getTotalcaloriesburned() {
        return totalcaloriesburned;
    }

    public void setTotalcaloriesburned(Integer totalcaloriesburned) {
        this.totalcaloriesburned = totalcaloriesburned;
    }
}

@Entity
class UserTable implements Serializable {
    @ColumnInfo(name = "userid")
    private Integer userid;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "surname")
    private String surname;
    @ColumnInfo(name = "email")
    private String email;
    @ColumnInfo(name = "dateofbirth")
    private String dateofbirth;
    @ColumnInfo(name = "height")
    private Integer height;
    @ColumnInfo(name = "weight")
    private Integer weight;
    @ColumnInfo(name = "gender")
    private String gender;
    @ColumnInfo(name = "address")
    private String address;
    @ColumnInfo(name = "postcode")
    private String postcode;
    @ColumnInfo(name = "levelofactivity")
    private Integer levelofactivity;
    @ColumnInfo(name = "stepspermile")
    private Integer stepspermile;

    UserTable(Integer userid, String username, String usersurname, String useremail, String userDOB, Integer userheight, Integer userweight, String usergender, String useraddress, String userpostcode, Integer userLOA, Integer userSPM) {
        this.userid = userid;
        this.name = username;
        this.surname = usersurname;
        this.email = useremail;
        this.dateofbirth = userDOB;
        this.height = userheight;
        this.weight = userweight;
        this.gender = usergender;
        this.address = useraddress;
        this.postcode = userpostcode;
        this.levelofactivity = userLOA;
        this.stepspermile = userSPM;
    }

    UserTable() {
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateofbirth() {
        return dateofbirth;
    }

    public void setDateofbirth(String dateofbirth) {
        this.dateofbirth = dateofbirth;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserpostcode() {
        return postcode;
    }

    public void setUserpostcode(String userpostcode) {
        this.postcode = userpostcode;
    }

    public Integer getLevelofactivity() {
        return levelofactivity;
    }

    public void setLevelofactivity(Integer levelofactivity) {
        this.levelofactivity = levelofactivity;
    }

    public Integer getStepspermile() {
        return stepspermile;
    }

    public void setStepspermile(Integer stepspermile) {
        this.stepspermile = stepspermile;
    }
}

@Dao
interface ReportTableDAO {
    @Query("SELECT * FROM reporttable")
    List<ReportTable> getAll();

    // @Query("SELECT * FROM reporttable WHERE userid LIKE :first AND " + "last_name LIKE :last LIMIT 1")
    // ReportTable findByFirstandLastName(String first, String last);

    @Query("SELECT * FROM reporttable WHERE reportid = :reportid LIMIT 1")
    ReportTable findByID(int reportid);

    @Insert
    void insertAll(ReportTable... report);

    @Insert
    long insert(ReportTable report);

    @Delete
    void delete(ReportTable report);

    @Update(onConflict = REPLACE)
    public void updateUsers(ReportTable... reports);

    @Query("DELETE FROM reporttable")
    void deleteAll();
}

@Database(entities = {ReportTable.class}, version = 2, exportSchema = false)
abstract class ReportTableDatabase extends RoomDatabase {

    public abstract ReportTableDAO reporttableDAO();

    private static volatile ReportTableDatabase INSTANCE;

    static ReportTableDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ReportTableDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), ReportTableDatabase.class, "reporttable_database").build();
                }
            }
        }
        return INSTANCE;
    }

}

@Entity
class DailyStepsClass{
    @PrimaryKey(autoGenerate = true)
    private int dscid;
    @ColumnInfo(name = "userid")
    private int userid;
    @ColumnInfo(name = "steps")
    private int steps;
    @ColumnInfo(name = "date")
    private String date;

    DailyStepsClass(int userid, int steps, String date) {
        this.userid = userid;
        this.steps = steps;
        this.date = date;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public Integer getSteps() {
        return steps;
    }

    public void setSteps(Integer steps) {
        this.steps = steps;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDscid() {
        return dscid;
    }

    public void setDscid(int dscid) {
        this.dscid = dscid;
    }
}

@Dao
interface DailyStepsClassDAO{
    @Query("SELECT * FROM dailystepsclass")
    List<DailyStepsClass> getAll();

    @Query("SELECT * FROM dailystepsclass WHERE dscid = :dscid")
    DailyStepsClass findByID(int dscid);

    @Insert
    void insertAll(DailyStepsClass... dailyStepsClasses);

    @Insert
    long insert(DailyStepsClass dailyStepsClass);

    @Delete
    void delete(DailyStepsClass dailyStepsClass);

    @Update(onConflict = REPLACE)
    public void updateUsers(DailyStepsClass... dailyStepsClasses);

    @Query("DELETE FROM dailystepsclass")
    void deleteAll();
}

@Database(entities = {DailyStepsClass.class}, version = 2, exportSchema = false)
abstract class DailyStepsClassDatabase extends RoomDatabase{

    public abstract DailyStepsClassDAO dailyStepsClassDAO();

    private static volatile DailyStepsClassDatabase INSTANCE;

    static DailyStepsClassDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DailyStepsClassDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), DailyStepsClassDatabase.class, "dailystepsclass_database").build();
                }
            }
        }
        return INSTANCE;
    }

}