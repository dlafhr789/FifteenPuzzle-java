import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import java.util.Random;
import java.util.Arrays;

public class FifteenPuzzle {
    // Swing 의 JFrame 을 상속받는 Game 클래스 생성
    public static class Game extends JFrame{
        private String title;
        private int dim;
        private int count;              // 움직인 횟수
        private int[][] target_state;   // 목표 상태
        private int[][] now_state;      // 현재 상태
        private int[] coor;             // 현재 0의 좌표
        private int width;              // 창 높이
        private int height;             // 창 너비

        private JPanel puzzle_board;
        private JPanel info;

        private JButton btn[][];
        private JButton reset_btn;
        private JButton shuffle_btn;
        private JLabel cnt_label;
        
        public Game(int d){
            this.title = "재미있는 15 퍼즐 게임";
            this.dim = d;
            this.count = 0;
            this.target_state = new int[this.dim][this.dim];
            this.now_state = new int[this.dim][this.dim];
            this.coor = new int[2];
            ResetState();;

            this.btn = new JButton[this.dim][this.dim];
            for(int i = 0; i < this.dim; i++){
                for(int j = 0; j < this.dim; j++){
                    btn[i][j] = new JButton(Integer.toString(this.now_state[i][j]));
                    if(this.now_state[i][j] == 0){
                        btn[i][j].setBackground(Color.gray);
                        btn[i][j].setText("");
                    }
                    btn[i][j].setSize(100, 100);
                    btn[i][j].setBackground(Color.white);
                    btn[i][j].setFont(new Font("consolas", Font.BOLD, 30));
                    final int index_i = i;
                    final int index_j = j;
                    btn[i][j].addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e){
                            if(Swap(index_i, index_j)){
                                SetBtnText();
                                SetCountText();

                                if(count > 0 && CompareArrays(now_state, target_state)){
                                    JOptionPane.showMessageDialog(null, "축하드립니다!!\n움직인 횟수 : " + count, "클리어!!", JOptionPane.INFORMATION_MESSAGE);
                                    count = 0;
                                    SetCountText();
                                }
                            }
                        }
                    });
                }
            }
            this.width = 700;
            this.height = 800;
            
            this.puzzle_board = new JPanel();
            this.puzzle_board.setBorder(new EmptyBorder(50, 50, 50, 50));

            this.info = new JPanel();

            // RESET 버튼 설정
            this.reset_btn = new JButton("RESET");
            this.reset_btn.setBackground(Color.white);
            this.reset_btn.setFont(new Font("consolas", Font.PLAIN, 15));
            // RESET 버튼 클릭했을 때
            this.reset_btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){
                    ResetState();
                    SetBtnText();
                    count = 0;
                    SetCountText();
                }
            });

            // SHUFFLE 버튼 설정
            this.shuffle_btn = new JButton("SHUFFLE");
            this.shuffle_btn.setBackground(Color.white);
            this.shuffle_btn.setFont(new Font("consolas", Font.PLAIN, 15));
            // SHUFFLE 버튼 클릭했을 때
            this.shuffle_btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ShuffleBord();
                }
            });

            // COUNT 라벨 설정
            this.cnt_label = new JLabel("이동 횟수 : " + this.count);
        }

        // (i, j)가 움직일 수 있으면 움직이는 메소드
        private boolean Swap(int i, int j){
            if(i == this.coor[0] && j == this.coor[1]) return false;        // 빈 칸을 클릭했을 때
            if(Math.abs(i-this.coor[0]) + Math.abs(j-this.coor[1]) == 1){   // 맨해튼 거리가 1일 때 (스왑 가능할 때)
                int temp = this.now_state[i][j];
                this.now_state[i][j] = this.now_state[this.coor[0]][this.coor[1]];
                this.now_state[this.coor[0]][this.coor[1]] = temp;
                this.coor[0] = i;
                this.coor[1] = j;
                this.count++;
                return true;
            }
            return false;
        }

        // 2차원 배열 2개를 비교
        private boolean CompareArrays(int[][] arr1, int[][] arr2){
            for(int i = 0; i < this.dim; i++){
                for(int j = 0; j < this.dim; j++){
                    if(arr1[i][j] != arr2[i][j]) return false;
                }
            }
            return true;
        }

        // now_state 에 따라 버튼 택스트와 컬러 설정
        private void SetBtnText(){
            for(int i = 0; i < this.dim; i++){
                for(int j = 0; j < this.dim; j++){
                    // 빈칸일 때
                    if(this.now_state[i][j] == 0){
                        this.btn[i][j].setText("");
                        this.btn[i][j].setBackground(Color.gray);
                        continue;
                    }
                    // 빈칸 아닐 때
                    this.btn[i][j].setText(Integer.toString(this.now_state[i][j]));
                    btn[i][j].setBackground(Color.white);
                }
            }
        }

        // count 에 따라 count_label 텍스트 설정
        private void SetCountText(){
            this.cnt_label.setText("이동 횟수 : " + this.count);
        }

        // 해당 2차원 배열이 풀이가 가능한지 판단하는 메소드
        // https://natejin.tistory.com/22
        private boolean IsSolvable(int[][] arr){
            int i_col = -1;
            int[] flat = new int[this.dim * this.dim];
            for(int i = 0; i < this.dim; i++){
                for(int j = 0; j < this.dim; j++){
                    if(arr[i][j] == 0) i_col = this.dim - i;
                    flat[i * this.dim + j] = arr[i][j];
                }
            }
            // 0을 찾지 못했으면 return false
            if(i_col == -1) return false;

            int cnt = 0;
            for(int i = 0; i < this.dim * this.dim; i++){
                if(flat[i] == 0) continue;
                for(int j = i; j < this.dim * this.dim; j++){
                    if(flat[j] == 0) continue;
                    if(flat[i] > flat[j]) cnt++;
                }
            }
            return (i_col % 2 == 0 && cnt % 2 == 1) || (i_col % 2 == 1 && cnt % 2 == 0);
        }

        // 랜덤한 값으로 퍼즐 재구성
        private void ShuffleBord(){
            Random rd = new Random();
            int[][] result;
            do {
                result = new int[this.dim][this.dim];
                boolean[] used = new boolean[this.dim * this.dim + 1];

                for (int i = 0; i < this.dim; i++) {
                    for (int j = 0; j < this.dim; j++) {
                        int randomN;
                        do {
                            randomN = rd.nextInt(this.dim * this.dim);
                        } while (used[randomN]);
                        result[i][j] = randomN;
                        if(randomN == 0) {
                            this.coor[0] = i;
                            this.coor[1] = j;
                        }
                        used[randomN] = true;
                    }
                }
            } while(!IsSolvable(result));

            this.count = 0;
            this.SetCountText();

//            for(int i = 0; i < this.dim)
            this.now_state = Arrays.copyOf(result, result.length);
            SetBtnText();
        }

        // 목표 상태로 퍼즐 재구성
        private void ResetState(){
            for(int i = 0; i < this.dim; i++){
                for(int j = 0; j < this.dim; j++){
                    if(i == 3 && j == 3) {
                        this.target_state[i][j] = 0;
                        this.now_state[i][j] = 0;
                        coor[0] = i;
                        coor[1] = j;
                        continue;
                    }
                    this.target_state[i][j] = i * this.dim + j + 1;
                    this.now_state[i][j] = i * this.dim + j + 1;
                }
            }
        }

        // 창 설정 후 띄우기
        public void DisplayGame(){
            setTitle(this.title);
            setSize(this.width, this.height);   // 창 크기 설정
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);     // 창 닫을 때 프로그램 종료
            setLocationRelativeTo(null);        // 창 띄울 때 중앙 배치
            setResizable(false);                // 창 크기조절

            // info 창 (reset, shuffle, count 뜨는 창) 설정
            add(this.info, BorderLayout.NORTH);     // 윗쪽에 배치
            this.info.setBorder(new EmptyBorder(50, 0, 0, 0));  // 상단 마진 설정
            this.info.add(this.reset_btn);          // reset 버튼 배치
            this.info.add(this.shuffle_btn);        // shuffle 버튼 배치
            this.info.add(this.cnt_label);          // count 라벨 배치

            // 퍼즐 보드 배치
            add(this.puzzle_board, BorderLayout.CENTER);       // 중앙 배치
            this.puzzle_board.setLayout(new GridLayout(this.dim, this.dim, 20, 20));        // 각각 20px 간격 설정
            // 각 버튼 배치
            for(int i = 0; i < this.dim; i++){
                for(int j = 0; j < this.dim; j++){
                    this.puzzle_board.add(btn[i][j]);
                }
            }

            SetBtnText();
            setVisible(true);   // 창 띄우기
        }
    }
    public static void main(String[] args) throws Exception {
        Game g = new Game(4);
        g.DisplayGame();
    }
}
