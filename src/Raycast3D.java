import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.stage.Stage;
import javafx.scene.canvas.*;
import javafx.scene.input.KeyEvent;

public class Raycast3D extends Application {
    
    private final int screen_width = 1000;
    private final int screen_height = 500;
    private final int column_width = 5;
    private final double fov = Math.PI / 2;
    //wasd
    private final int[] keys = {0,0,0,0};
    private float v = 0.05f;
    private float x = 1;
    private float y = 1;
    private float dir = (float) (Math.PI/4);
    private int[][] map;
    private final Color ground_color = Color.rgb(170, 189, 140);
    private final Color ceiling_color = Color.rgb(157, 191, 158);
    private final Color[] wall_color = {null, Color.rgb(233, 227, 180), Color.rgb(102, 143, 128), Color.rgb(105, 109, 125)};
    private final Color map_color = Color.rgb(57, 92, 107, 0.5);
    
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

        stage.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getText().equals("w")) keys[0] = 1;
                if (ke.getText().equals("a")) keys[1] = 1;
                if (ke.getText().equals("s")) keys[2] = 1;
                if (ke.getText().equals("d")) keys[3] = 1;
                ke.consume();
            }
        });
        
        stage.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getText().equals("w")) keys[0] = 0;
                if (ke.getText().equals("a")) keys[1] = 0;
                if (ke.getText().equals("s")) keys[2] = 0;
                if (ke.getText().equals("d")) keys[3] = 0;
                ke.consume();
            }
        });
        
        map = new int[][] {
            {1,1,1,1,1,1,2,3,3,3,3,3,3,3,3,3},
            {1,0,0,0,0,0,2,0,0,0,0,3,0,0,0,1},
            {1,0,0,0,0,0,2,0,3,3,0,3,0,2,0,1},
            {1,0,0,0,0,0,2,0,3,0,0,3,0,2,0,1},
            {1,0,0,0,0,0,2,0,3,0,3,3,0,2,2,1},
            {1,0,0,0,0,0,0,0,0,0,0,2,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,2,0,2,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,2,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        };
        
        AnimationTimer timer = new AnimationTimer() {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            @Override
            public void handle(long now) {
                float prev_x = x;
                float prev_y = y;
                if (keys[0] == 1) {
                    x += v*Math.cos(dir);
                    y += v*Math.sin(dir);
                } else if (keys[2] == 1) {
                    x -= v*Math.cos(dir);
                    y -= v*Math.sin(dir);
                }
                
                if (map[(int)y][(int)x] != 0) {
                    x = prev_x;
                    y = prev_y;
                }
                
                if (keys[1] == 1) dir -= v;
                else if (keys[3] == 1) dir += v;
                
                gc.setFill(Color.BLACK);
                gc.fillRect(0, 0, screen_width, screen_height);
                for (int i = screen_height/2; i <= screen_height; i+=column_width) {
                    float p = 0.75f - 3*((float)i - screen_height/2) / screen_height/2;
                    gc.setFill(adjust_color(ground_color, p));
                    gc.fillRect(0, i, screen_width, column_width);
                    gc.setFill(adjust_color(ceiling_color, p));
                    gc.fillRect(0, screen_height/2 - (i - screen_height/2), screen_width, column_width);
                }

                for (int col = 0; col < screen_width/column_width; col++) {
                    double ray_angle = dir - fov/2.0 + (col*(float)column_width/(float)screen_width)*fov;
                    float distance_to_wall = 0;
                    double test_x, test_y;
                    int wall_type;
                    while (true) {
                        test_x = x + distance_to_wall*Math.cos(ray_angle);
                        test_y = y + distance_to_wall*Math.sin(ray_angle);
                        if (map[(int)test_y][(int)test_x] != 0) {
                            wall_type = map[(int)test_y][(int)test_x];
                            break;
                        } else {
                            distance_to_wall += 0.1f;
                        }
                    }
                    
                    //fish eye correction
                    distance_to_wall = (float) (distance_to_wall*Math.cos(Math.abs(dir - ray_angle)));
                    float ceiling_height = screen_height / 2 - screen_height/distance_to_wall;
                    gc.setFill(adjust_color(wall_color[wall_type], distance_to_wall/10));
                    gc.fillRect(col*column_width, ceiling_height, column_width, screen_height - 2*ceiling_height);
                }
                //draw mini map
                gc.setFill(map_color);
                for (int i = 0; i < map.length; ++i) {
                    for (int j = 0; j < map[0].length; ++j) {
                        if (map[i][j] != 0) {
                            gc.fillRect(10+j*15, 10+i*15, 15, 15);
                        }
                    }
                }
                gc.setFill(Color.rgb(127, 47, 47, 0.5));
                //3 Points for a triangle pointed forward
                double[] poly_x = new double[]{10+x*15, 10+x*15-5, 10+x*15+5};
                double[] poly_y = new double[]{10+y*15, 10+y*15-15, 10+y*15-15};
                //Rotate all the points according to the point of view
                double xr = (poly_x[1] - poly_x[0])*Math.cos(dir-Math.PI/2) - (poly_y[1] - poly_y[0])*Math.sin(dir-Math.PI/2) + poly_x[0];
                double yr = (poly_x[1] - poly_x[0])*Math.sin(dir-Math.PI/2) + (poly_y[1] - poly_y[0])*Math.cos(dir-Math.PI/2) + poly_y[0];
                poly_x[1] = xr; poly_y[1] = yr;
                xr = (poly_x[2] - poly_x[0])*Math.cos(dir-Math.PI/2) - (poly_y[2] - poly_y[0])*Math.sin(dir-Math.PI/2) + poly_x[0];
                yr = (poly_x[2] - poly_x[0])*Math.sin(dir-Math.PI/2) + (poly_y[2] - poly_y[0])*Math.cos(dir-Math.PI/2) + poly_y[0];
                poly_x[2] = xr; poly_y[2] = yr;
                gc.fillPolygon(poly_x, poly_y, 3);
            }
        };
        timer.start();
        
    }
    
    //p is the amount of adjustment. p=1 for black, p=0 for no change
    private Color adjust_color(Color base, float p) {
        p = p > 1 ? 1 : p;
        return Color.rgb((int)(255*base.getRed()*(1-p)), (int)(255*base.getGreen()*(1-p)), (int)(255*base.getBlue()*(1-p)));
    }
}