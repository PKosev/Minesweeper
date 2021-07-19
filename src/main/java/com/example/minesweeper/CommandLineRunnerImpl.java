package com.example.minesweeper;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Component
public class CommandLineRunnerImpl implements CommandLineRunner {

    private final BufferedReader bufferedReader;

    public CommandLineRunnerImpl() {
        this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Enter the Difficulty Level");
        int level = Integer.parseInt(bufferedReader.readLine());
        int matrixSize = setSize(level);
        int minesCount = setCount(level);
        Set<String> minesList = new HashSet<>();
        String[][] matrix = new String[matrixSize][matrixSize];
        generateMatrix(matrix, matrixSize);
        printMatrix(matrix, matrixSize);
        System.out.println("Enter your move");
        String[] positions = bufferedReader.readLine().split("\\s+");
        String firstPosition = positions[0]+" "+positions[1];
        generateBombs(minesList, minesCount, matrixSize,firstPosition);
        checkPosition(matrix, positions, minesList);

        int countOfCellsToBeChecked = checkForCells(matrix, positions, minesList);
        while (countOfCellsToBeChecked != 0){
            countOfCellsToBeChecked = checkForCells(matrix, positions, minesList);
        }
        printMatrix(matrix, matrixSize);

        boolean youWin = false;
        while (!youWin) {
            System.out.println("Enter your move");
            positions = bufferedReader.readLine().split("\\s+");
            checkPosition(matrix, positions, minesList);
            if (minesList.contains(positions[0] + " " + positions[1])){
                printMatrix(matrix, matrixSize);
                System.out.println();
                System.out.println("You lost!");
                return;
            }
            countOfCellsToBeChecked = checkForCells(matrix, positions, minesList);
            while (countOfCellsToBeChecked != 0){
                countOfCellsToBeChecked = checkForCells(matrix, positions, minesList);
            }
            youWin = checkForWin(matrix, minesCount);
            printMatrix(matrix, matrixSize);
            if (youWin){
                System.out.println("Congratulation you won!");
            }
        }
    }

    private boolean checkForWin(String[][] matrix, int minesCount) {
        int count = 0;
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix.length; col++) {
                if(matrix[row][col].equals("-")){
                    count++;
                }
            }
        }
        return count == minesCount;
    }


    private int checkForCells(String[][] matrix, String[] positions, Set<String> minesList) {
        int count = 0;
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix.length; col++) {
                if(matrix[row][col].equals("C")){
                    positions[0] = String.valueOf(row);
                    positions[1] = String.valueOf(col);
                    adjacentSafeCells(positions, minesList, matrix);
                    count++;
                }
            }
        }
        return count;
    }

    private void checkPosition(String[][] matrix, String[] positions, Set<String> minesList) {
            String currentPosition = positions[0] + " " + positions[1];
            int row;
            int col;
            if (minesList.contains(currentPosition)){
                for (String mine:minesList) {
                    row = Integer.parseInt(mine.split("\\s+")[0]);
                    col = Integer.parseInt(mine.split("\\s+")[1]);
                    matrix[row][col] = "*";
                }
                return;
            }
            adjacentSafeCells(positions, minesList, matrix);

    }

    private void adjacentSafeCells(String[] positions, Set<String> minesList, String[][] matrix) {
        int count = 0;
        int row = Integer.parseInt(positions[0]);
        int col = Integer.parseInt(positions[1]);
        int startingRow = Integer.parseInt(positions[0]) -1;
        int startingCol = Integer.parseInt(positions[1]) -1;

                for (int i = startingRow; i < startingRow+3 ; i++) {
                    if(i <0 || i > matrix.length){
                        continue;
                    }
                    for (int j = startingCol; j < startingCol+3; j++) {
                        if(j < 0 || j > matrix.length){
                            continue;
                        }
                        if (minesList.contains(i+" "+j)){
                            count++;
                        }
                    }
                }
        if (count != 0) {
            matrix[row][col] = String.valueOf(count);
        }else {
            for (int i = startingRow; i < startingRow+3 ; i++) {
                    if(i <0 || i >= matrix.length){
                        continue;
                    }
                    for (int j = startingCol; j < startingCol+3; j++) {
                        if(j < 0 || j >= matrix.length){
                            break;
                        }else if(i == row && j==col){
                            break;
                        }else if(matrix[i][j].equals("-")){
                            matrix[i][j] = "C";
                        }
                    }
                }
            matrix[row][col] = String.valueOf(0);
            }
        }


    private void generateBombs(Set<String> minesList, int minesCount, int matrixSize, String firstPosition) {
        Random rand = new Random();
        for (int i = 0; i < minesCount; i++) {
            int row = rand.nextInt(matrixSize);
            int col = rand.nextInt(matrixSize);
            String bomb = row+" "+col;
            if (minesList.contains(bomb) || firstPosition.equals(bomb)){
                i--;
            }else {
                minesList.add(bomb);
            }
        }
    }

    private void printMatrix(String[][] matrix, int size) {
        System.out.println("Current Status of Board :");
        StringBuilder builder = new StringBuilder("     ");
        for (int i = 0; i < size; i++) {
            builder.append(i).append("  ");
        }
        System.out.println(builder);
        int i = 0;
        for (String[] arr : matrix) {
            if (i >= 10) {
                System.out.print(i +"   ");
            }else {
                System.out.print(i +"    ");
            }
            int j= 0;
            for (String symbol : arr) {
                if (j>=10){
                    System.out.print(symbol+"   ");
                }else {
                    System.out.print(symbol+"  ");
                }
                   j++;
            }
            i++;
            System.out.println();
        }
    }


    private void generateMatrix(String[][] matrix, int matrixSize) {
        for (int row = 0; row < matrixSize; row++) {
            for (int col = 0; col < matrixSize; col++) {
                matrix[row][col] = "-";
            }
        }
    }

    private int setSize(int level) {
        switch (level){
            case 0:
                return 9;
            case 1:
                return  16;
            case 2:
                return 24;
        }
        return 0;
    }

    private int setCount(int level) {
        switch (level){
            case 0:
                return 10;
            case 1:
                return  40;
            case 2:
                return 99;
        }
        return 0;
    }
}
