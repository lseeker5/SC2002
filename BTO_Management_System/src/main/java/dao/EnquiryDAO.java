package dao;

import models.Applicant;
import models.Enquiry;
import models.Project;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import utils.Config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EnquiryDAO {
    private static final Logger LOGGER = Logger.getLogger(EnquiryDAO.class.getName());

    public static Enquiry getEnquiryById(int targetID) {
        try (FileInputStream fis = new FileInputStream(Config.ENQUIRY_DB_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet("Sheet1");

            for (Row row: sheet) {
                if (row.getRowNum() == 0) continue;

                int id = (int) row.getCell(0).getNumericCellValue();

                if (id == targetID) {
                    String nric = row.getCell(1).getStringCellValue();
                    Applicant applicant = ApplicantDAO.getApplicantByNRIC(nric);

                    String name = row.getCell(2).getStringCellValue();
                    Project project = ProjectDAO.getProjectByName(name);

                    String message = row.getCell(3).getStringCellValue();
                    String reply = row.getCell(4).getStringCellValue();

                    Enquiry enquiry = new Enquiry(
                            applicant,
                            project,
                            message
                    );
                    enquiry.setId(id);
                    enquiry.setReply(reply);

                    return enquiry;
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to get enquiry by ID");
        }
        return null;
    }

    public static List<Enquiry> getEnquiryByApplicantNric(String targetNRIC) {
        List<Enquiry> enquiries = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(Config.ENQUIRY_DB_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet("Sheet1");

            for (Row row: sheet) {
                if (row.getRowNum() == 0) continue;

                String nric = row.getCell(1).getStringCellValue();

                if (nric.equals(targetNRIC)) {
                    int id = (int) row.getCell(0).getNumericCellValue();
                    Applicant applicant = ApplicantDAO.getApplicantByNRIC(nric);

                    String name = row.getCell(2).getStringCellValue();
                    Project project = ProjectDAO.getProjectByName(name);

                    String message = row.getCell(3).getStringCellValue();
                    String reply = row.getCell(4).getStringCellValue();

                    Enquiry enquiry = new Enquiry(
                            applicant,
                            project,
                            message
                    );
                    enquiry.setId(id);
                    enquiry.setReply(reply);

                    enquiries.add(enquiry);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to get enquiry by ID");
        }

        return enquiries;
    }

    public static boolean addEnquiry(Enquiry enquiry) {
        boolean added = false;

        try (FileInputStream fis = new FileInputStream(Config.ENQUIRY_DB_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet("Sheet1");
            int rowCount = sheet.getLastRowNum();
            Row newRow = sheet.createRow(++rowCount);

            int id = enquiry.getId();
            String nric = enquiry.getApplicant().getNric();
            String name = enquiry.getProject().getName();
            String message = enquiry.getMessage();
            String reply = enquiry.getReply();

            newRow.createCell(0).setCellValue(id);
            newRow.createCell(1).setCellValue(nric);
            newRow.createCell(2).setCellValue(name);
            newRow.createCell(3).setCellValue(message);
            newRow.createCell(4).setCellValue(reply);

            fis.close();

            try (FileOutputStream fos = new FileOutputStream(Config.ENQUIRY_DB_PATH)) {
                workbook.write(fos);
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to add enquiry");
        }

        return added;
    }

    public static boolean updateEnquiryById(int targetID, String newMessage) {
        boolean updated = false;

        try (FileInputStream fis = new FileInputStream(Config.ENQUIRY_DB_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet("Sheet1");

            for (Row row: sheet) {
                if (row.getRowNum() == 0) continue;

                int id = (int) row.getCell(0).getNumericCellValue();

                if (id == targetID) {
                    row.getCell(3).setCellValue(newMessage);
                    updated = true;
                    break;
                }
            }

            try (FileOutputStream fos = new FileOutputStream(Config.ENQUIRY_DB_PATH)) {
                workbook.write(fos);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to update user");
        }

        return updated;
    }

    public static boolean deleteEnquiryById(int targetID) {
        boolean deleted = false;

        try (FileInputStream fis = new FileInputStream(Config.ENQUIRY_DB_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet("Sheet1");

            for (Row row: sheet) {
                if (row.getRowNum() == 0) continue;

                int id = (int) row.getCell(0).getNumericCellValue();

                if (id == targetID) {
                    int i = row.getRowNum();
                    removeRow(sheet, i);
                    deleted = true;
                    break;
                }
            }

            try (FileOutputStream fos = new FileOutputStream(Config.ENQUIRY_DB_PATH)) {
                workbook.write(fos);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to delete user");
        }

        return deleted;
    }

    private static void removeRow(Sheet sheet, int rowIndex) {
        int lastRowNum = sheet.getLastRowNum();
        if (rowIndex >= 0 && rowIndex < lastRowNum) {
            sheet.shiftRows(rowIndex + 1, lastRowNum, -1);
        } else if (rowIndex == lastRowNum) {
            Row removingRow = sheet.getRow(rowIndex);
            if (removingRow != null) {
                sheet.removeRow(removingRow);
            }
        }
    }
}
