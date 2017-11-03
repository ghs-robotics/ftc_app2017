final float[][] KERNEL = {{ -1, 0, 1},
                          { -2, 0, 2},
                          { -1, 0, 1}};
final int TAPE_LENGTH_TOLERANCE = 2;
final float KERNEL_SCALER = 1;
final float TAPE_THRESHOLD = 1000;
final float COLOR_CONTRAST_FACTOR = 2; //4 is pretty safe
final int FUDGE_AMOUNT = 4;
final int SEARCH_RADIUS = 10;
/* //old function values
final float FUNCTION_A_A = 8001.3;
final float FUNCTION_A_B = 1.880;
final float FUNCTION_A_C = 301.5;
final float FUNCTION_B_A = 1000.0;
final float FUNCTION_B_B = 1.7695;
final float FUNCTION_B_C = 1.1096;
final float FUNCTION_C_A = 9999;
final float FUNCTION_C_B = -7.88;
final float FUNCTION_C_C = 258;
final float FUNCTION_D_A = 3000.0;
final float FUNCTION_D_B = -.8011;
final float FUNCTION_D_C = .6550;
*/
final float FUNCTION_A_A = 49533.9; //tape y pos
final float FUNCTION_A_B = -8.12939;
final float FUNCTION_A_C = -164.262;
final float FUNCTION_B_A = 2810.64; //tape width
final float FUNCTION_B_B = -7.33581;
final float FUNCTION_B_C = 1.96587;
final float FUNCTION_C_A = 43237.7; //ball y centers
final float FUNCTION_C_B = -8.3531;
final float FUNCTION_C_C = -158.189;
final float FUNCTION_D_A = 8763.09; //offset from tape center to ball centers
final float FUNCTION_D_B = -9.99816;
final float FUNCTION_D_C = 5.43091;