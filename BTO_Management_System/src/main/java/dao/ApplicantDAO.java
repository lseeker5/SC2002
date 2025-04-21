package dao;

import models.Applicant;
import models.MaritalStatus;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import utils.Config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApplicantDAO {
    private static final Logger LOGGER = Logger.getLogger(ApplicantDAO.class.getName());

    protected static MaritalStatus validateMaritalStatus(String maritalStatusString) {
        if(maritalStatusString.trim().equals("Single")) { return MaritalStatus.SINGLE; }
        if(maritalStatusString.trim().equals("Married")) { return MaritalStatus.MARRIED; }

        return null;
    }

    public static Applicant getApplicantByNRIC(String targetNRIC) {
        try (FileInputStream fis = new FileInputStream(Config.APPLICANT_DB_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet("Sheet1");

            for (Row row: sheet) {
                if (row.getRowNum() == 0) continue;

                String nric = row.getCell(1).getStringCellValue();

                if (nric.equals(targetNRIC)) {
                    String name = row.getCell(0).getStringCellValue().trim();
                    int age = (int) row.getCell(2).getNumericCellValue();
                    MaritalStatus maritalStatus = validateMaritalStatus(row.getCell(3).getStringCellValue());
                    String password = row.getCell(4).getStringCellValue().trim();

                    Applicant applicant = new Applicant(
                            name,
                            nric,
                            age,
                            maritalStatus
                    );
                    applicant.changePassword(password);
                    return applicant;
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to get applicant by NRIC");
        }
        return null;
    }
}
