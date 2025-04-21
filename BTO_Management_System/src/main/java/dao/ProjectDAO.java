package dao;

import models.FlatType;
import models.Project;
import org.apache.poi.ss.formula.functions.Log;
import utils.Config;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ProjectDAO {
    private static final Logger LOGGER = Logger.getLogger(ProjectDAO.class.getName());

    protected static FlatType typeValidator(String type) {
        if (type.trim().equals("2-Room")) { return FlatType.TWOROOM; }
        if (type.trim().equals("3-Room")) { return FlatType.THREEROOM; }

        return null;
    }

    protected static String typeToString(FlatType type) {
        if (type.equals(FlatType.TWOROOM)) { return "2-Room\n"; }
        if (type.equals(FlatType.THREEROOM)) { return "3-Room\n"; }

        return null;
    }

    public static List<Project> getAllProjects() {
        List<Project> projects = new LinkedList<>();
        try (FileInputStream fis = new FileInputStream(Config.PROJECT_DB_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet("Sheet1");

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                String name = row.getCell(0).getStringCellValue().trim();
                String neighborhood = row.getCell(1).getStringCellValue().trim();
                FlatType type1 = typeValidator(row.getCell(2).getStringCellValue());
                int unit1 = (int) row.getCell(3).getNumericCellValue();
                int price1 = (int) row.getCell(4).getNumericCellValue();
                FlatType type2 = typeValidator(row.getCell(5).getStringCellValue());
                int unit2 = (int) row.getCell(6).getNumericCellValue();
                int price2 = (int) row.getCell(7).getNumericCellValue();
                Date open = row.getCell(8).getDateCellValue();
                Date close = row.getCell(9).getDateCellValue();
                String manager = row.getCell(10).getStringCellValue().trim();
                int officerSlot = (int) row.getCell(11).getNumericCellValue();
                String[] officers = row.getCell(12).getStringCellValue().trim().split(",");

                projects.add(new Project(
                        name,
                        neighborhood,
                        manager,
                        open,
                        close,
                        officerSlot));
                projects.getLast().addFlatType(type1, unit1, price1);
                projects.getLast().addFlatType(type2, unit2, price2);
                for (String officer : officers) {
                    projects.getLast().addOfficer(officer);
                }
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to view all projects");
        }

        return projects;
    }

    public static Project getProjectByName(String targetName) {

        try (FileInputStream fis = new FileInputStream(Config.PROJECT_DB_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet("Sheet1");

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                String name = row.getCell(0).getStringCellValue().trim();

                if (name.equals(targetName)) {
                    String neighborhood = row.getCell(1).getStringCellValue().trim();
                    FlatType type1 = typeValidator(row.getCell(2).getStringCellValue());
                    int unit1 = (int) row.getCell(3).getNumericCellValue();
                    int price1 = (int) row.getCell(4).getNumericCellValue();
                    FlatType type2 = typeValidator(row.getCell(5).getStringCellValue());
                    int unit2 = (int) row.getCell(6).getNumericCellValue();
                    int price2 = (int) row.getCell(7).getNumericCellValue();
                    Date open = row.getCell(8).getDateCellValue();
                    Date close = row.getCell(9).getDateCellValue();
                    String manager = row.getCell(10).getStringCellValue().trim();
                    int officerSlot = (int) row.getCell(11).getNumericCellValue();
                    String[] officers = row.getCell(12).getStringCellValue().trim().split(",");

                    Project project = new Project(
                            name,
                            neighborhood,
                            manager,
                            open,
                            close,
                            officerSlot
                    );
                    project.addFlatType(type1, unit1, price1);
                    project.addFlatType(type2, unit2, price2);
                    for (String officer : officers) {
                        project.addOfficer(officer);
                    }

                    return project;
                }
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to view all projects");
        }

        return null;
    }

    public static boolean addProject (Project project) {
        boolean added = false;

        try (FileInputStream fis = new FileInputStream(Config.PROJECT_DB_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet("Sheet1");
            int rowCount = sheet.getLastRowNum();
            Row newRow = sheet.createRow(++rowCount);

            newRow.createCell(0).setCellValue(project.getName());
            newRow.createCell(1).setCellValue(project.getNeighborhood());

            List<FlatType> types = new ArrayList<>(project.getFlatTypes().keySet());
            if (!project.getFlatTypes().isEmpty()) {
                FlatType type1 = types.getFirst();
                newRow.createCell(2).setCellValue(typeToString(type1));
                newRow.createCell(3).setCellValue(project.getFlatTypes().get(type1));
                newRow.createCell(4).setCellValue(project.getFlatPrices().get(type1));
            }
            if (project.getFlatTypes().size() > 1) {
                FlatType type2 = types.getLast();
                newRow.createCell(5).setCellValue(typeToString(type2));
                newRow.createCell(6).setCellValue(project.getFlatTypes().get(type2));
                newRow.createCell(7).setCellValue(project.getFlatPrices().get(type2));
            }

            newRow.createCell(8).setCellValue(project.getOpeningDate());
            newRow.createCell(9).setCellValue(project.getClosingDate());
            newRow.createCell(10).setCellValue(project.getManager());
            newRow.createCell(11).setCellValue(project.getOfficerSlots());
            newRow.createCell(12).setCellValue(project.getOfficers().toString());

            fis.close();
            try (FileOutputStream fos = new FileOutputStream(Config.PROJECT_DB_PATH)) {
                workbook.write(fos);
            }

        } catch (IOException e) {
           LOGGER.log(Level.SEVERE, "Failed to add project");
        }

        return false;
    }
}
