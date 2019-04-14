import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.stage.Stage;
import javafx.scene.canvas.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class Raycast3D extends Application {
    
    private final int screen_width = 800;
    private final int screen_height = 500;
    private final int column_width = 10;
    private final double fov = Math.PI / 2;
    private float v = 0.1f;
    private float x = 5;
    private float y = 5;
    private float dir;
    private int[][] map;
    
    public static void main(String[] args) { launch(args); }
    
    @Override
    public void start(Stage stage) {
        stage.setTitle("Raycast3D");
        
        Group root = new Group();
        Scene s = new Scene(root, screen_width, screen_height, Color.BLACK);

        final Canvas canvas = new Canvas(screen_width, screen_height);                   
        root.getChildren().add(canvas);
        stage.setScene(s);
        stage.show();
        
        stage.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println(event);
            }
        });
        
        stage.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getText()) {
                case "w":
                    x += v*Math.cos(dir);
                    y += v*Math.sin(dir);
                    break;
                case "s":
                    x -= v*Math.cos(dir);
                    y -= v*Math.sin(dir);
                    break;
                case "a":
                    dir -= 0.1;
                    break;
                case "d":
                    dir += 0.1;
                    break;
                }
            }
        });
        
        map = new int[][] {
            {1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,0,0,0,0,1},
            {1,0,0,1,1,1,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1},
        };
        
        AnimationTimer timer = new AnimationTimer() {
            private long prev;
            GraphicsContext gc = canvas.getGraphicsContext2D();
            @Override
            public void handle(long now) {
                double d = (now - prev) / Math.pow(10, 6);
                prev = now;
                gc.setFill(Color.BLACK);
                gc.fillRect(0, 0, screen_width, screen_height);
                for (int col = 0; col < screen_width/column_width; col++) {
                    double ray_angle = dir - fov/2.0 + (col*(float)column_width/(float)screen_width)*fov;
                    float distance_to_wall = 0;
                    double test_x, test_y;
                    while (true) {
                        test_x = x + distance_to_wall*Math.cos(ray_angle);
                        test_y = y + distance_to_wall*Math.sin(ray_angle);
                        if (map[(int)test_y][(int)test_x] != 0) {
                            break;
                        } else {
                            distance_to_wall += 0.1f;
                        }
                    }
                    
                    //fish eye correction
                    distance_to_wall = (float) (distance_to_wall*Math.cos(Math.abs(dir - ray_angle)));
                    
                    //wall color
                    //(x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
                    int g = 255 - (int) (distance_to_wall* (255 - 50) / (10) + 50);
                    
                    float ceiling_height = screen_height / 2 - screen_height/distance_to_wall;
                    
                    gc.setFill(Color.rgb(g, g, g));
                    gc.fillRect(col*column_width, 0, column_width, screen_height);
                    gc.setFill(Color.CADETBLUE);
                    //draw ceiling
                    gc.fillRect(col*column_width, 0, column_width, ceiling_height);
                    //draw floor
                    gc.fillRect(col*column_width, screen_height - ceiling_height, column_width, ceiling_height);
                    
                }
                //draw mini map
                gc.setFill(Color.rgb(47, 127, 53, 0.5));
                for (int i = 0; i < map.length; ++i) {
                    for (int j = 0; j < map[0].length; ++j) {
                        if (map[i][j] == 1) {
                            gc.fillRect(10+j*15, 10+i*15, 15, 15);
                        }
                    }
                }
                gc.setFill(Color.rgb(127, 47, 47, 0.5));
                gc.fillRect(10+x*15, 10+y*15, 5, 5);
                
            }
        };
        timer.start();
        
    }

}
