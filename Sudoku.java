package cs445.a3;

import java.util.List;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Sudoku {

    // this variable will hold the starting state of the board for the program to refer to
    private static int[][] originalBoard;

    static boolean isFullSolution(int[][] board) {
        // check that all cells have been filled
        for(int i=8; i>=0; i--)
        {
            for(int j=8; j>=0; j--)
            {
                if(board[i][j] == 0)
                {
                    return false;
                }
            }
        }
        // this nested for loop will check every square on the board
        // if no squares are empty, then the board is conmpleted and we can return true
        return true;
    }

    static boolean reject(int[][] board) {
        // if the board cannot be correctly completed, return true
        // this method tells the program to remove the last added answer and next the previous answer
        // then the program will see if it can extend the solution

        // result will hold the value that is returned.  True for a rejected path, and False for an extendable path
        boolean result = false;
        // the region starts will hold the values that will allow me to check a specific 3x3 cell for duplicates
        int regionColStart = 0;
        int regionRowStart = 0;

        // this nested for loop will check every cell, and see if there is a duplicate value in the same row, column, or region
        for(int i = 0; i < 9; i++)
        {
            for(int j = 0; j < 9; j++)
            {
                for(int k = 0; k < 9; k++)
                {
                    // if there is a duplicate value in the same row, and the duplicate is not 0, return true
                    if(board[i][j] == board[i][k] && j!=k)
                    {
                        result = true;
                        if(board[i][j] != 0)
                            {
                                return result;
                            }
                    }

                    // if there is a duplicate value in the same column, and the duplicate value is not 0, return true
                    if(board[i][j] == board[k][j] && i!=k)
                    {
                        result = true;
                        if(board[i][j] != 0)
                            {
                                return result;
                            }
                    }
                }

                // the region starts will be set using modulus to make sure that only a single 3x3 region is checked for duplicates
                regionColStart = j - (j % 3);
                regionRowStart = i - (i % 3);

                // this nested for loop will check a single 3x3 region for duplicates that are not 0
                for(int row = regionRowStart; row < regionRowStart+3; row++)
                {
                    for(int col = regionColStart; col < regionColStart+3; col++)
                    {
                        // if there is a duplicate that is not the value at i, j and the value is not 0, return true
                        if(board[i][j] == board[row][col] && !(i == row && j == col))
                        {
                            result = true;
                            if(board[i][j] != 0)
                            {
                                return result;
                            }
                        }
                    }
                }
            }
        }
        
        // if the nested for loops have not found any duplicate values that are not 0, then the path can be extended, return false
        result = false;
        return result;
    }

    static int[][] extend(int[][] board) {
        // if the board solution can be extended, then return the new state of the board 
        
        // create a temp array
        int temp[][] = new int[9][9];
        // this boolean will check that if a zero is found, that it is the first 0
        boolean foundZero = false;

        // i is the index of the row and j is the index of the column
        for(int i=0; i<9; i++)
        {
            for(int j=0; j<9; j++)
            {
                // copy the entire board into the temp array
                temp[i][j] = board[i][j];

                // if there has not been a zero found yet, and the current lacation is a zero, change foundZero to true and 
                // change the value at this location to 1
                if(!foundZero && temp[i][j]==0)
                {
                    foundZero = true;
                    temp[i][j] = 1;
                }
            }
        }

        // if no 0s were found, return null
        if(foundZero == false)
        {
            return null;
        }

        // return the temp array
        return temp;
    }

    static int[][] next(int[][] board) {
        // incriment the last value extended
        int i = 0,j = 0;

        // this loop will go through the board until it finds a 0 value.  As it moves through the board it incriments its location
        while(board[i][j] != 0)
        {
            // incriment which column you are in
            j++;

            // if j reaches 0, reset j to 0 and incriment the row
            if(j>8)
            {
                j = 0;
                i++;
            }
            // if the row exceeds the edge of the board, set i and j to 8 and break the loop
            if(i>8)
            {
                i = 8;
                j = 8;
                break;
            }
        }

        // while the new board and the original board are equal, decriment through the board in the opposite direction that you incrimented
        while(board[i][j] == originalBoard[i][j])
        {
            // decriment along a row until j is less than 0
            j--;
            // once j is less than 0, reset it to 8 and decriment the row
            if(j<0)
            {
                j=8;
                i--;
            }

            // if the row is decrimented beyond the board, then return null
            if(i<0)
            {
                return null;
            }
        }

        // incriment the value of the last changed value from extend, if the newValue exceeds 9, return null
        int newValue = board[i][j] + 1;
        if(newValue > 9)
        {
            return null;
        }
        // if the newValue does not exceed 9, set the last extended location to the newValue
        board[i][j] = newValue;
        // return the new board
        return board;
    }

    static void testIsFullSolution() {
        // This tests that a sudoku board has been correctly filled or not
        System.out.println("The following are testing the isFullSolution method: ");
        int[][] solved = new int[][]{
            {4,3,5,2,6,9,7,8,1},
            {6,8,2,5,7,1,4,9,3},
            {1,9,7,8,3,4,5,6,2},
            {8,2,6,1,9,5,3,4,7},
            {3,7,4,6,8,2,9,1,5},
            {9,5,1,7,4,3,6,2,8},
            {5,1,9,3,2,6,8,7,4},
            {2,4,8,9,5,7,1,3,6},
            {7,6,3,4,1,8,2,5,9}
        };

        int[][] unSolved = new int[][]{
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0}
        };

        boolean result = isFullSolution(solved);
        boolean result2 = isFullSolution(unSolved);
        System.out.println("Expected to return true for solved board and got: " + result);
        System.out.println("Expected to return false for unsolved board and got: " + result2);
    }

    static void testReject() {
        // this tests every case that the reject method can produce to make sure it works properly
        System.out.println("\nThe following are testing the Reject Method: ");

        int[][] testNoDup = new int [][]{
            {0,1,2,0,0,0,0,0,0},
            {3,4,5,0,0,0,0,0,0},
            {6,7,8,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
        };

        int[][] testDupRow = new int [][]{
            {1,1,2,0,0,0,0,0,0},
            {3,4,5,0,0,0,0,0,0},
            {6,7,8,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
        };

        int[][] testDupCol = new int [][]{
            {3,1,2,0,0,0,0,0,0},
            {3,4,5,0,0,0,0,0,0},
            {6,7,8,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
        };
        int[][] testDupReg = new int [][]{
            {4,1,2,0,0,0,0,0,0},
            {3,4,5,0,0,0,0,0,0},
            {6,7,8,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
        };
        boolean resultNoDup = reject(testNoDup);
        boolean resultDupRow = reject(testDupRow);
        boolean resultDupCol = reject(testDupCol);
        boolean resultDupReg = reject(testDupReg);
        System.out.println("Expected to return false for unsolved board without duplicates and got: " + resultNoDup);
        System.out.println("Expected to return true for unsolved board with duplicate in row and got: " + resultDupRow);
        System.out.println("Expected to return true for unsolved board with duplicate in column and got: " + resultDupCol);
        System.out.println("Expected to return true for unsolved board with duplicate in region and got: " + resultDupReg);

    }

    static void testExtend() {
        // test each case that extend can return to make sure it works properly
        System.out.println("\nThe following are testing the Extend Method: ");
        int[][] test = new int [][]{
            {0,1,2,0,0,0,0,0,0},
            {3,4,5,0,0,0,0,0,0},
            {6,7,8,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
        };
        int[][] testFull = new int [][]{
            {1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1},
        };


        int[][] result = extend(test);
        System.out.println("Expected first cell to equal 1 and got: " + result[0][0]);
        int[][] result2 = extend(result);
        System.out.println("Expected fourth cell to equal 1 and got: " + result2[0][3]);
        System.out.println("Expected extend to return null and got: " + extend(testFull));
    }

    static void testNext() {
        // Test every case
        System.out.println("\nThe following are testing the Next Method: ");
        originalBoard = new int [][]{
            {0,1,2,0,0,0,0,0,0},
            {3,4,5,0,0,0,0,0,0},
            {6,7,8,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
        };
        
        int[][] test = new int [][]{
            {1,1,2,0,0,0,0,0,0},
            {3,4,5,0,0,0,0,0,0},
            {6,7,8,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
        };

        int[][] test2 = new int [][]{
            {9,1,2,0,0,0,0,0,0},
            {3,4,5,0,0,0,0,0,0},
            {6,7,8,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
        };

        System.out.println("Expecting first cell to equal 2 and got: " + test[0][0]);
        printBoard(next(test2));
    }

    static void printBoard(int[][] board) {
        if (board == null) {
            System.out.println("No assignment");
            return;
        }
        for (int i = 0; i < 9; i++) {
            if (i == 3 || i == 6) {
                System.out.println("----+-----+----");
            }
            for (int j = 0; j < 9; j++) {
                if (j == 2 || j == 5) {
                    System.out.print(board[i][j] + " | ");
                } else {
                    System.out.print(board[i][j]);
                }
            }
            System.out.print("\n");
        }
    }

    static int[][] readBoard(String filename) {
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get(filename), Charset.defaultCharset());
        } catch (IOException e) {
            return null;
        }
        int[][] board = new int[9][9];
        int val = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                try {
                    val = Integer.parseInt(Character.toString(lines.get(i).charAt(j)));
                } catch (Exception e) {
                    val = 0;
                }
                board[i][j] = val;
            }
        }
        return board;
    }

    static int[][] solve(int[][] board) {
        if (reject(board)) return null;
        if (isFullSolution(board)) return board;
        int[][] attempt = extend(board);
        while (attempt != null) {
            int[][] solution = solve(attempt);
            if (solution != null) return solution;
            attempt = next(attempt);
        }
        return null;
    }

    public static void main(String[] args) {
        if (args[0].equals("-t")) {
            testIsFullSolution();
            testReject();
            testExtend();
            testNext();
        } else {
            int[][] board = readBoard(args[0]);
            originalBoard = board;
            printBoard(board);
            System.out.println("Solution:");
            printBoard(solve(board));
        }
    }
}

