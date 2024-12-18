package net.peak.agent.flexibilityTradingAgent.behaviour;

public class IEEEBusMatrix {

    // Matrices representing different grid sizes for simulation and testing
    int[][] matrix33 = new int[34][34];  // 33-bus system matrix
    double[][] impedanceMatrix3 = new double[4][4];  // 3-bus impedance matrix
    double[][] impedanceMatrix5 = new double[6][6];  // 5-bus impedance matrix
    double[][] impedanceMatrix33 = new double[34][34];  // 33-bus impedance matrix
    double[][] impedanceMatrix119 = new double[120][120];  // 119-bus impedance matrix
    double[][] impedanceMatrix10 = new double[11][11];  // 10-bus impedance matrix
    int[][] matrix3 = new int[4][4];  // 3-bus system matrix
    int[][] matrix5 = new int[6][6];  // 5-bus system matrix
    int[][] matrix119 = new int[120][120];  // 119-bus system matrix
    int[][] matrix10 = new int[11][11];  // 10-bus system matrix

    // Methods to retrieve the connection matrices for different grid sizes
    public int[][] get33Matrix() {
        return matrix33;
    }

    public int[][] get5Matrix() {
        return matrix5;
    }

    public int[][] get119Matrix() {
        return matrix119;
    }

    public int[][] get10Matrix() {
        return matrix10;
    }

    public int[][] get3Matrix() {
        return matrix3;
    }

    // Constructor initializes the matrices with default connections and impedance values
    public IEEEBusMatrix() {
        // Connections for a 119-bus system based on IEEE standards
        int[][] connections119 = {
            {0, 1}, {1, 2}, {2, 3}, {3, 4}, {3, 5}, {5, 6}, {6, 7}, {7, 8}, {8, 9}, {9, 10},
            {5, 29}, {29, 30}, {30, 31}, {31, 32}, {32, 33}, {33, 34}, {34, 35}, {35, 36},
            {36, 37}, {37, 38}, {38, 39}, {39, 40}, {40, 41}, {41, 42}, {42, 43}, {43, 44},
            {30, 47}, {47, 48}, {48, 49}, {49, 50}, {50, 51}, {51, 52}, {52, 53}, {53, 54},
            {54, 55}, {31, 45}, {45, 46}, {30, 56}, {56, 57}, {57, 58}, {58, 59}, {59, 60},
            {60, 61}, {61, 62}, {62, 63}, {3, 11}, {11, 12}, {12, 13}, {13, 14}, {14, 15},
            {15, 16}, {16, 17}, {17, 18}, {12, 19}, {19, 20}, {20, 21}, {21, 22}, {22, 23},
            {23, 24}, {24, 25}, {25, 26}, {26, 27}, {27, 28}, {2, 64}, {64, 65}, {65, 66},
            {66, 67}, {67, 68}, {68, 69}, {69, 70}, {70, 71}, {71, 72}, {72, 73}, {73, 74},
            {74, 75}, {75, 76}, {76, 77}, {77, 78}, {65, 79}, {79, 80}, {80, 81}, {81, 82},
            {82, 83}, {83, 84}, {84, 85}, {85, 86}, {80, 87}, {87, 88}, {88, 89}, {66, 90},
            {90, 91}, {91, 92}, {92, 93}, {93, 94}, {94, 95}, {95, 96}, {92, 97}, {97, 98},
            {98, 99}, {99, 100}, {2, 101}, {101, 102}, {102, 103}, {103, 104}, {104, 105},
            {105, 106}, {106, 107}, {107, 108}, {108, 109}, {109, 110}, {110, 111}, {111, 112},
            {111, 113}, {113, 114}, {101, 115}, {115, 116}, {116, 117}, {117, 118}, {118, 119}
        };

        // Connections for a 5-bus system
        int[][] connections5 = {
            {0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}
        };

        // Connections for a 3-bus system
        int[][] connections3 = {
            {0, 1}, {1, 2}, {2, 3}
        };

        // Connections for a 10-bus system
        int[][] connections10 = {
            {0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}, {5, 6}, {6, 7}, {7, 8}, {8, 9}, {9, 10}
        };

        // Connections for a 33-bus system
        int[][] connections33 = {
            {0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}, {5, 6}, {6, 7}, {7, 8}, {8, 9}, {9, 10},
            {10, 11}, {11, 12}, {12, 13}, {13, 14}, {14, 15}, {15, 16}, {16, 17}, {17, 18},
            {2, 19}, {3, 23}, {6, 26}, {19, 20}, {20, 21}, {21, 22}, {23, 24}, {24, 25},
            {26, 27}, {27, 28}, {28, 29}, {29, 30}, {30, 31}, {31, 32}, {32, 33}
        };

        // Impedance values for the 33-bus system
        impedanceMatrix33[0][1] = 0.0047;
        impedanceMatrix33[1][2] = 0.02511;
        impedanceMatrix33[2][3] = 0.01864;
        impedanceMatrix33[3][4] = 0.01941;
        impedanceMatrix33[4][5] = 0.0707;
        impedanceMatrix33[5][6] = 0.06188;
        impedanceMatrix33[6][7] = 0.02351;
        impedanceMatrix33[7][8] = 0.074;
        impedanceMatrix33[8][9] = 0.074;
        impedanceMatrix33[9][10] = 0.0065;
        impedanceMatrix33[10][11] = 0.01238;
        impedanceMatrix33[11][12] = 0.1155;
        impedanceMatrix33[12][13] = 0.07129;
        impedanceMatrix33[13][14] = 0.0526;
        impedanceMatrix33[14][15] = 0.0545;
        impedanceMatrix33[15][16] = 0.1721;
        impedanceMatrix33[16][17] = 0.0574;
        impedanceMatrix33[1][18] = 0.01565;
        impedanceMatrix33[18][19] = 0.13554;
        impedanceMatrix33[19][20] = 0.04784;
        impedanceMatrix33[20][21] = 0.09373;
        impedanceMatrix33[21][22] = 0.03083;
        impedanceMatrix33[22][23] = 0.07091;
        impedanceMatrix33[23][24] = 0.07011;
        impedanceMatrix33[24][25] = 0.01034;
        impedanceMatrix33[25][26] = 0.01447;
        impedanceMatrix33[26][27] = 0.09337;
        impedanceMatrix33[27][28] = 0.07006;
        impedanceMatrix33[28][29] = 0.02585;
        impedanceMatrix33[29][30] = 0.0963;
        impedanceMatrix33[30][31] = 0.03619;
        impedanceMatrix33[31][32] = 0.05302;

        // Fill the 33-bus matrix with connections
        for (int[] connection33 : connections33) {
            int start = connection33[0];
            int end = connection33[1];
            matrix33[start][end] = 1;
        }

        // Fill the 5-bus matrix with connections
        for (int[] connection5 : connections5) {
            int start = connection5[0];
            int end = connection5[1];
            matrix5[start][end] = 1;
        }

        // Fill the 119-bus matrix with connections
        for (int[] connection119 : connections119) {
            int start = connection119[0];
            int end = connection119[1];
            matrix119[start][end] = 1;
        }

        // Fill the 10-bus matrix with connections
        for (int[] connection10 : connections10) {
            int start = connection10[0];
            int end = connection10[1];
            matrix10[start][end] = 1;
        }

        // Fill the 3-bus matrix with connections
        for (int[] connection3 : connections3) {
            int start = connection3[0];
            int end = connection3[1];
            matrix3[start][end] = 1;
        }

        // Optional: Print the 33-bus matrix for verification
        // for (int i = 0; i < 33; i++) {
        //     for (int j = 0; j < 33; j++) {
        //         System.out.print(matrix33[i][j] + " ");
        //     }
        //     System.out.println();
        // }
    }
}
