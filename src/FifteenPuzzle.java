import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;
import java.io.IOException;

import java.util.Arrays;
import java.util.Random;

import javax.sound.sampled.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import java.lang.Thread;

public class FifteenPuzzle {
    // 추상 클래스 AudioPlayer
    public static abstract class AudioPlayer {
        protected Clip backgroundClip;
        protected Clip effectClip;

        // 추상 메서드
        public abstract void playMoveEffect();
        public abstract void playErrorEffect();
        public abstract void playResetEffect();
        public abstract void playShuffleEffect();
        public abstract void playRestartButtonEffect();
        public abstract void playExitButtonEffect();

        public void playBackgroundMusic(String filePath) {
            try {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
                backgroundClip = AudioSystem.getClip();
                backgroundClip.open(audioInputStream);
                backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
                backgroundClip.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                ex.printStackTrace();
            }
        }

        public void stopBackgroundMusic() {
            if (backgroundClip != null && backgroundClip.isRunning()) {
                backgroundClip.stop();
            }
        }

        protected void playEffect(String filePath) {
            try {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
                effectClip = AudioSystem.getClip();
                effectClip.open(audioInputStream);
                effectClip.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                ex.printStackTrace();
            }
        }
    }

    // 구체적인 AudioPlayer 구현 클래스
    public static class ConcreteAudioPlayer extends AudioPlayer {
        // 이동시 효과음
        @Override
        public void playMoveEffect() {
            playEffect("swoosh.wav");
        }

        // 움직일 수 없는 버튼 선택시 효과음
        @Override
        public void playErrorEffect() {
            playEffect("wrong.wav");
        }

        // 리셋 버튼 선택시 효과음
        @Override
        public void playResetEffect() {
            playEffect("blip.wav");
        }

        // 셔플 버튼 선택시 효과음
        @Override
        public void playShuffleEffect() {
            playEffect("skip.wav");
        }

        // 재시작 버튼 선택시 효과음
        @Override
        public void playRestartButtonEffect() {
            playEffect("pop.wav");
        }

        // 종료 버튼 선택시 효과음
        @Override
        public void playExitButtonEffect() {
            playEffect("click.wav");
        }

    }

    // 퍼즐 클리어시 나타나는 화면
    public static class CongratulationsFrame extends JFrame {
        private JPanel contentPane;
        private Game gameInstance;
        private AudioPlayer audioPlayer;
        private Clip backgroundClip; // 배경음악 클립

        public CongratulationsFrame(Game gameInstance, int count, AudioPlayer audioPlayer) {
            this.gameInstance = gameInstance;
            this.audioPlayer = audioPlayer;

            // 게임 창의 배경음악 중지
            gameInstance.audioPlayer.stopBackgroundMusic();
            // 배경 음악 재생
            playBackgroundMusic("goodresult.wav");

            setTitle("축하합니다!");
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setBounds(100, 100, 700, 800);
            setLocationRelativeTo(null); // 화면 중앙에 배치

            contentPane = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Image img = new ImageIcon("forest2.png").getImage();
                    g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                }
            };
            contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

            JLabel congratsLabel = new JLabel("<html><div style='text-align: center;'>Congratulations!!" +
                    "<br>Move Count: " + count + "</div></html>", SwingConstants.CENTER);
            congratsLabel.setFont(new Font("Verdana", Font.BOLD, 40));
            congratsLabel.setForeground(Color.darkGray);
            congratsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPane.add(Box.createVerticalGlue());
            contentPane.add(congratsLabel);

            JButton restartButton = new JButton("RESTART");
            restartButton.setFont(new Font("Verdana", Font.BOLD, 40));
            restartButton.setPreferredSize(new Dimension(200, 70));
            restartButton.setOpaque(false);
            restartButton.setContentAreaFilled(false);
            restartButton.setForeground(Color.BLACK);
            restartButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            restartButton.setOpaque(true);
            restartButton.setBackground(new Color(255, 255, 255, 128));
            restartButton.setFocusable(false);
            restartButton.setRolloverEnabled(false);

            restartButton.addActionListener(e -> {
                gameInstance.audioPlayer.playRestartButtonEffect(); // Restart 효과음 재생
                gameInstance.restartGame(); // 게임 재시작
                dispose(); // 현재 축하 창 닫기
            });

            JButton exitButton = new JButton("EXIT");
            exitButton.setFont(new Font("Verdana", Font.BOLD, 40));
            exitButton.setPreferredSize(new Dimension(200, 70));
            exitButton.setOpaque(false);
            exitButton.setContentAreaFilled(false);
            exitButton.setForeground(Color.BLACK);
            exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            exitButton.setOpaque(true);
            exitButton.setBackground(new Color(255, 255, 255, 128));
            exitButton.setFocusable(false);
            exitButton.setRolloverEnabled(false);

            exitButton.addActionListener(e -> {
                gameInstance.audioPlayer.playExitButtonEffect(); // Exit 효과음 재생
                System.exit(0); // 모든 창 닫고 애플리케이션 종료
            });

            contentPane.add(Box.createRigidArea(new Dimension(0, 20)));
            contentPane.add(restartButton);
            contentPane.add(Box.createRigidArea(new Dimension(0, 20)));
            contentPane.add(exitButton);
            contentPane.add(Box.createVerticalGlue());

            setLocationRelativeTo(null);
            setResizable(false);
            setContentPane(contentPane);
            setVisible(true);

            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    // 창이 닫힐 때 배경 음악 중지
                    stopBackgroundMusic();
                }
            });
        }
        // 배경 음악 재생 메서드
        private void playBackgroundMusic(String filePath) {
            try {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
                backgroundClip = AudioSystem.getClip();
                backgroundClip.open(audioInputStream);

                // 배경 음악 재생
                backgroundClip.start();

                // 배경 음악 재생이 끝나면 클립을 닫음
                backgroundClip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        backgroundClip.close();
                    }
                });
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                ex.printStackTrace();
            }
        }

        // 배경 음악 중지 메서드
        private void stopBackgroundMusic() {
            if (backgroundClip != null && backgroundClip.isRunning()) {
                backgroundClip.stop();
                backgroundClip.close(); // 리소스 해제
            }
        }

    }


    // 제일 처음 게임 시작했을 때 화면
    public static class StartScreen extends JFrame {
        private JPanel contentPane;
        private AudioPlayer audioPlayer;

        public StartScreen() {
            audioPlayer = new ConcreteAudioPlayer();
            audioPlayer.playBackgroundMusic("backgroundmusic4.wav");

            setTitle("number puzzle game");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setBounds(100, 100, 700, 800);
            contentPane = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Image img = new ImageIcon("forest.png").getImage();
                    g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                }
            };
            contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
            setContentPane(contentPane);

            JLabel titleLabel = new JLabel("FUNNY 15 PUZZLE GAME", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Verdana", Font.BOLD, 40));
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPane.add(Box.createVerticalGlue());
            contentPane.add(titleLabel);

            JButton startButton = new JButton("GAME START");
            startButton.setFont(new Font("Verdana", Font.BOLD, 40));
            startButton.setPreferredSize(new Dimension(200, 70));
            startButton.setOpaque(false);
            startButton.setContentAreaFilled(false);
            startButton.setForeground(Color.BLACK);
            startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            startButton.setOpaque(true);
            startButton.setBackground(new Color(255, 255, 255, 128));
            startButton.setFocusable(false);
            startButton.setRolloverEnabled(false);

            startButton.addActionListener(e -> {
                audioPlayer.playEffect("click1.wav");
                audioPlayer.stopBackgroundMusic();
                Game game = new Game(4, audioPlayer);
                game.DisplayGame();
                dispose();
            });

            contentPane.add(Box.createRigidArea(new Dimension(0, 20)));
            contentPane.add(startButton);
            contentPane.add(Box.createVerticalGlue());

            setLocationRelativeTo(null);
            setResizable(false);
            setVisible(true);
        }

        public static void main(String[] args) {
            SwingUtilities.invokeLater(StartScreen::new);
        }
    }

    public static class ImagePanel extends JPanel {
        private Image img;

        public ImagePanel(Image img) {
            this.img = img;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) {
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    // Runnable 인터페이스를 사용한 타이머 기능
    public static class TimeRunnable implements Runnable {
        private boolean stopFlag = false;
        JLabel timer_label;
        public TimeRunnable(JLabel tl) {
            this.timer_label =  tl;
        }
        public void setStopFlag(){
            System.out.println("타이머 쓰래드 종료");
            stopFlag = true;
        }
        @Override
        public void run() {
            System.out.println("타이머 쓰래드 실행");
            int time = 0;
            while (!stopFlag) {
                try {
                    Thread.sleep(1000);
                    time++;
                    System.out.println(time + "초 경과");
                    timer_label.setText("Time: " + time + "s");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            stopFlag = false;
        }
        public void test() {
            System.out.println("test print");
        }
    }


    public static class Game extends JFrame {
        private String title;
        private int dim;
        private int count;
        private int[][] target_state;
        private int[][] now_state;
        private int[] coor;
        private int width;
        private int height;
        private AudioPlayer audioPlayer;

        private JPanel puzzle_board;
        private ImagePanel info;

        private JButton[][] btn;
        private JButton reset_btn;
        private JButton shuffle_btn;
        private JLabel cnt_label;

        private JLabel timer_label; // 타이머 라벨 추가
        private Timer timer; // 타이머 변수 추가


        Thread tr;

        public Game(int d, AudioPlayer audioPlayer) {
            System.out.println("본 게임 시작");
            this.title = "재미있는 15 퍼즐 게임";
            this.dim = d;
            this.count = 0;
            this.target_state = new int[this.dim][this.dim];
            this.now_state = new int[this.dim][this.dim];
            this.coor = new int[2];
            this.audioPlayer = audioPlayer;
            audioPlayer.playBackgroundMusic("backgroundmusic3.wav");
            ResetState();


            this.info = new ImagePanel(new ImageIcon("header.png").getImage()) {
                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(Game.this.width, 70);
                }
            };

            this.info.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);

            // Timer 라벨 설정
            this.timer_label = new JLabel("Time: 0s", SwingConstants.RIGHT);
            this.timer_label.setForeground(Color.WHITE);
            this.timer_label.setPreferredSize(new Dimension(200, 50));
            this.timer_label.setFont(new Font("Fixedsys", Font.BOLD, 20));
            this.timer_label.setHorizontalAlignment(SwingConstants.RIGHT); // 오른쪽 정렬
            this.timer_label.setMaximumSize(new Dimension(Integer.MAX_VALUE, timer_label.getPreferredSize().height)); // 최대 너비 설정
            gbc.gridx = 3; // 위치 설정 (오른쪽 끝)
            gbc.anchor = GridBagConstraints.EAST; // 오른쪽 정렬
            this.info.add(timer_label, gbc);

            TimeRunnable _timeRunnable = new TimeRunnable(timer_label);
            this.tr = new Thread(_timeRunnable);
            tr.start();

            this.btn = new JButton[this.dim][this.dim];
            for (int i = 0; i < this.dim; i++) {
                for (int j = 0; j < this.dim; j++) {
                    btn[i][j] = new JButton(Integer.toString(this.now_state[i][j])) {
                        @Override
                        protected void paintComponent(Graphics g) {
                            if (getModel().isPressed()) {
                                g.setColor(new Color(200, 200, 200, 160));
                            } else {
                                g.setColor(new Color(255, 255, 255, 200));
                            }
                            g.fillRect(0, 0, getWidth(), getHeight());
                            super.paintComponent(g);
                        }
                    };

                    if (this.now_state[i][j] == 0) {
                        btn[i][j].setBackground(new Color(128, 128, 128, 200));
                        btn[i][j].setText("");
                    } else {
                        btn[i][j].setBackground(new Color(255, 255, 255, 200));
                    }
                    btn[i][j].setPreferredSize(new Dimension(100, 100));
                    btn[i][j].setFont(new Font("Fixedsys", Font.BOLD, 50));
                    btn[i][j].setFocusPainted(false);
                    btn[i][j].setOpaque(true);
                    btn[i][j].setContentAreaFilled(false);

                    final int index_i = i;
                    final int index_j = j;

                    btn[i][j].addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (Swap(index_i, index_j)) {
                                audioPlayer.playMoveEffect();
                                SetBtnText();
                                SetCountText();

                                // 게임 클리어 시 축하 메시지 출력 후 게임 다시 시작
                                if (count > 0 && CompareArrays(now_state, target_state)) {
                                    CongratulationsFrame congratsFrame = new CongratulationsFrame(Game.this, count, audioPlayer);
                                    congratsFrame.setVisible(true);
                                    _timeRunnable.setStopFlag();
                                    dispose(); // 현재 게임 창 닫기
                                    count = 0;
                                }
                            }
                            else {
                                audioPlayer.playErrorEffect();
                            }
                        }
                    });
                }
            }

            this.width = 700;
            this.height = 800;


            this.puzzle_board = new ImagePanel(new ImageIcon("forest.png").getImage());
            this.puzzle_board.setBorder(new EmptyBorder(50, 50, 50, 50));
            this.puzzle_board.setLayout(new GridLayout(this.dim, this.dim, 20, 20));

            for (int i = 0; i < this.dim; i++) {
                for (int j = 0; j < this.dim; j++) {
                    this.puzzle_board.add(btn[i][j]);
                }
            }




            // Reset 버튼 설정
            this.reset_btn = new JButton("Reset") {
                @Override
                protected void paintComponent(Graphics g) {
                    if (getModel().isPressed()) {
                        g.setColor(new Color(200, 200, 200, 0));
                    } else {
                        g.setColor(new Color(255, 255, 255, 0));
                    }
                    g.fillRect(0, 0, getWidth(), getHeight());
                    super.paintComponent(g);
                }

            };
            this.reset_btn.setPreferredSize(new Dimension(150, 50));
            this.reset_btn.setFont(new Font("Fixedsys", Font.BOLD, 25));
            this.reset_btn.setForeground(Color.WHITE);
            this.reset_btn.setFocusPainted(false);
            this.reset_btn.setOpaque(true);
            this.reset_btn.setContentAreaFilled(false);
            this.reset_btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    audioPlayer.playResetEffect();
                    count = 0;
                    SetCountText();
                    ResetState();
                    SetBtnText();
                    _timeRunnable.setStopFlag();
                    tr.start();
                }
            });
            gbc.gridx = 0; // 위치 설정 (왼쪽 첫 번째)
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST; // 왼쪽 정렬
            this.info.add(reset_btn, gbc);



            // Shuffle 버튼 설정
            this.shuffle_btn = new JButton("Shuffle") {
                @Override
                protected void paintComponent(Graphics g) {
                    if (getModel().isPressed()) {
                        g.setColor(new Color(200, 200, 200, 0));
                    } else {
                        g.setColor(new Color(255, 255, 255, 0));
                    }
                    g.fillRect(0, 0, getWidth(), getHeight());
                    super.paintComponent(g);
                }

            };

            // 셔플 버튼 설정
            this.shuffle_btn.setPreferredSize(new Dimension(150, 50));
            this.shuffle_btn.setFont(new Font("Fixedsys", Font.BOLD, 25));
            this.shuffle_btn.setForeground(Color.WHITE);
            this.shuffle_btn.setFocusPainted(false);
            this.shuffle_btn.setOpaque(true);
            this.shuffle_btn.setContentAreaFilled(false);
            this.shuffle_btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    audioPlayer.playShuffleEffect();
                    count = 0;
                    SetCountText();
                    ShuffleBord();
                    SetBtnText();
                    _timeRunnable.setStopFlag();
                    tr.start();
                }
            });
            gbc.gridx = 1; // 위치 설정 (왼쪽 두 번째)
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST; // 왼쪽 정렬 유지
            this.info.add(shuffle_btn, gbc);

            // Move Count 라벨 설정
            this.cnt_label = new JLabel("Move count : 0", SwingConstants.RIGHT);
            this.cnt_label.setForeground(Color.WHITE);
            this.cnt_label.setPreferredSize(new Dimension(200, 50));
            this.cnt_label.setFont(new Font("Fixedsys", Font.BOLD, 25));
            this.cnt_label.setHorizontalAlignment(SwingConstants.RIGHT); // 오른쪽 정렬
            this.cnt_label.setMaximumSize(new Dimension(Integer.MAX_VALUE, cnt_label.getPreferredSize().height)); // 최대 너비 설정
            gbc.gridx = 2; // 위치 설정 (오른쪽 끝)
            gbc.anchor = GridBagConstraints.EAST; // 오른쪽 정렬
            this.info.add(cnt_label, gbc);


            // 정보 창 추가
            getContentPane().add(this.info, BorderLayout.NORTH);
            getContentPane().add(this.puzzle_board, BorderLayout.CENTER);

            setTitle(this.title);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(this.width, this.height);
            setResizable(false);
            setLocationRelativeTo(null);
            setVisible(true);
        }

        // (i, j)가 움직일 수 있으면 움직이는 메소드
        private boolean Swap(int i, int j) {
            // 빈 칸을 클릭했을 때
            if (i == this.coor[0] && j == this.coor[1]) {
                System.out.println("자기자신은 움직일 수 없습니다.");
                return false;
            }
            // 맨해튼 거리가 1일 때 (스왑 가능할 때)
            if (Math.abs(i - this.coor[0]) + Math.abs(j - this.coor[1]) == 1) {
                int temp = this.now_state[i][j];
                this.now_state[i][j] = this.now_state[this.coor[0]][this.coor[1]];
                this.now_state[this.coor[0]][this.coor[1]] = temp;
                this.coor[0] = i;
                this.coor[1] = j;
                this.count++;
                System.out.println("블럭을 움직였습니다.");
                System.out.println("움직인 횟수 : " + count);

                return true;
            }
            return false;
        }

        // 2차원 배열 2개를 비교
        private boolean CompareArrays(int[][] arr1, int[][] arr2) {
            for (int i = 0; i < this.dim; i++) {
                for (int j = 0; j < this.dim; j++) {
                    if (arr1[i][j] != arr2[i][j])
                        return false;
                }
            }
            System.out.println("퍼즐 완성!!");
            return true;
        }

        // now_state 에 따라 버튼 택스트와 컬러 설정
        private void SetBtnText() {
            for (int i = 0; i < this.dim; i++) {
                for (int j = 0; j < this.dim; j++) {
                    // 빈칸일 때
                    if (this.now_state[i][j] == 0) {
                        this.btn[i][j].setText("");
                        this.btn[i][j].setBackground(Color.gray);
                        continue;
                    }
                    // 빈칸 아닐 때
                    this.btn[i][j].setText(Integer.toString(this.now_state[i][j]));
                    btn[i][j].setBackground(Color.lightGray);
                }
            }
        }

        // count 에 따라 count_label 텍스트 설정
        private void SetCountText() {
            this.cnt_label.setText(" Move Count : " + this.count);
        }


        // 해당 2차원 배열이 풀이가 가능한지 판단하는 메소드
        // https://natejin.tistory.com/22
        private boolean IsSolvable(int[][] arr) {
            int i_col = -1;
            int[] flat = new int[this.dim * this.dim];
            for (int i = 0; i < this.dim; i++) {
                for (int j = 0; j < this.dim; j++) {
                    if (arr[i][j] == 0)
                        i_col = this.dim - i;
                    flat[i * this.dim + j] = arr[i][j];
                }
            }
            // 0을 찾지 못했으면 return false
            if (i_col == -1)
                return false;

            // 랜덤으로 생성된 퀴즈가 풀이 가능한지 판단
            int cnt = 0;
            for (int i = 0; i < this.dim * this.dim; i++) {
                if (flat[i] == 0)
                    continue;
                for (int j = i; j < this.dim * this.dim; j++) {
                    if (flat[j] == 0)
                        continue;
                    if (flat[i] > flat[j])
                        cnt++;
                }
            }

            // true : 가능, false : 불가능
            return (i_col % 2 == 0 && cnt % 2 == 1) || (i_col % 2 == 1 && cnt % 2 == 0);
        }

        // 랜덤한 값으로 퍼즐 재구성
        private void ShuffleBord() {
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
                        if (randomN == 0) {
                            this.coor[0] = i;
                            this.coor[1] = j;
                        }
                        used[randomN] = true;
                    }
                }
            } while (!IsSolvable(result));

            this.count = 0;
            this.SetCountText();

            this.now_state = Arrays.copyOf(result, result.length);
            SetBtnText();
        }

        // 게임을 다시 시작할 때 이전에 재생 중이던 음악 중지 및 새로운 게임 시작
        private void restartGame() {
            audioPlayer.stopBackgroundMusic(); // 이전 음악 중지
            new Game(4, audioPlayer).DisplayGame(); // 새 게임 인스턴스 생성 및 표시
            dispose(); // 현재 게임 프레임 닫기
        }

        // 목표 상태로 퍼즐 재구성
        private void ResetState() {
            for (int i = 0; i < this.dim; i++) {
                for (int j = 0; j < this.dim; j++) {
                    if (i == this.dim - 1 && j == this.dim - 1) {
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
        public void DisplayGame() {
            // 창 제목 설정
            setTitle(this.title);
            // 창 크기 설정
            setSize(this.width, this.height);
            // 창 닫을 때 프로그램 종료
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            // 창 띄울 때 중앙 배치
            setLocationRelativeTo(null);
            // 창 크기조절
            setResizable(false);

            // info 창 (reset, shuffle, count 뜨는 창) 설정
            add(this.info, BorderLayout.NORTH); // 윗쪽에 배치
            // 상단 마진 설정
            this.info.setBorder(new EmptyBorder(20, 0, 20, 0));
            // reset 버튼 배치
            this.info.add(this.reset_btn);
            // shuffle 버튼 배치
            this.info.add(this.shuffle_btn);
            // count 라벨 배치
            this.info.add(this.cnt_label);

            // 퍼즐 보드 배치
            add(this.puzzle_board, BorderLayout.CENTER); // 중앙 배치
            // 각각 20px 간격 설정
            this.puzzle_board.setLayout(new GridLayout(this.dim, this.dim, 20, 20));
            // 각 버튼 배치
            for (int i = 0; i < this.dim; i++) {
                for (int j = 0; j < this.dim; j++) {
                    this.puzzle_board.add(btn[i][j]);
                }
            }

            SetBtnText();

        }

    }

    // 메인 메소드
    public static void main(String[] args) throws Exception {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("재미있는 16퍼즐 시작.");
        // 게임 시작 화면 띄우기
        new StartScreen();
    }
}