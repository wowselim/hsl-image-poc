package co.selim.blubb;

import co.selim.hslimage.Color;
import co.selim.hslimage.ColorUtils;
import co.selim.hslimage.HSLImage;
import javafx.application.Application;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.Duration;
import java.time.Instant;

public class Main extends Application {
    private HSLImage target;
    private Canvas sourceCanvas = new Canvas(640, 480);
    private Canvas targetCanvas = new Canvas(640, 480);

    private FloatProperty hue = new SimpleFloatProperty(1);
    private FloatProperty saturation = new SimpleFloatProperty(1);
    private FloatProperty luminance = new SimpleFloatProperty(1);

    private ToggleGroup group = new ToggleGroup();
    private Debounce repaintDebounce = new Debounce(this::repaint);

    private void drawImage(HSLImage img, Canvas canvas) {
        GraphicsContext context = canvas.getGraphicsContext2D();
        Instant drawingStart = Instant.now();
        for (int y = 0; y < canvas.getHeight(); y++) {
            for (int x = 0; x < canvas.getWidth(); x++) {
                int[] rgbComponents = ColorUtils.toRgbInts(img.getPixel((int) (x * (img.getWidth() / canvas.getWidth())), (int) (y * (img.getHeight() / canvas.getHeight()))));
                context.setFill(javafx.scene.paint.Color.rgb(rgbComponents[0], rgbComponents[1], rgbComponents[2]));
                context.fillRect(x, y, 1, 1);
            }
        }
        System.out.println("Image painting took " + Duration.between(drawingStart, Instant.now()).toMillis() + "ms");
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void repaint() {
        Color color = (Color) group.getSelectedToggle().getUserData();
        Instant processingStart = Instant.now();
        target.setHSL(color, hue.get(), saturation.get(), luminance.get());
        System.out.println("Pixel processing took " + Duration.between(processingStart, Instant.now()).toMillis() + "ms");

        drawImage(target, targetCanvas);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ToggleButton redToggle = new ToggleButton("Red");
        redToggle.setUserData(Color.RED);
        redToggle.setToggleGroup(group);
        ToggleButton orangeToggle = new ToggleButton("Orange");
        orangeToggle.setUserData(Color.ORANGE);
        orangeToggle.setToggleGroup(group);
        ToggleButton yellowToggle = new ToggleButton("Yellow");
        yellowToggle.setUserData(Color.YELLOW);
        yellowToggle.setToggleGroup(group);
        ToggleButton greenToggle = new ToggleButton("Green");
        greenToggle.setUserData(Color.GREEN);
        greenToggle.setToggleGroup(group);
        ToggleButton aquaToggle = new ToggleButton("Aqua");
        aquaToggle.setUserData(Color.AQUA);
        aquaToggle.setToggleGroup(group);
        ToggleButton blueToggle = new ToggleButton("Blue");
        blueToggle.setUserData(Color.BLUE);
        blueToggle.setToggleGroup(group);
        ToggleButton purpleToggle = new ToggleButton("Purple");
        purpleToggle.setUserData(Color.PINK);
        purpleToggle.setToggleGroup(group);
        ToggleButton magentaToggle = new ToggleButton("Magenta");
        magentaToggle.setUserData(Color.MAGENTA);
        magentaToggle.setToggleGroup(group);

        Slider hueSlider = new Slider(0.7, 1.3, 1);
        hueSlider.setShowTickLabels(true);
        hue.bind(hueSlider.valueProperty());
        hueSlider.valueProperty().addListener((obs, oldV, newV) -> {
            repaintDebounce.debounce();
        });
        Label hueLabel = new Label("Hue");
        HBox.setHgrow(hueSlider, Priority.ALWAYS);
        HBox hueSliderContainer = new HBox(10, hueLabel, hueSlider);

        Slider saturationSlider = new Slider(0, 2, 1);
        saturationSlider.setShowTickLabels(true);
        saturation.bind(saturationSlider.valueProperty());
        saturationSlider.valueProperty().addListener((obs, oldV, newV) -> {
            repaintDebounce.debounce();
        });
        Label saturationLabel = new Label("Saturation");
        HBox.setHgrow(saturationSlider, Priority.ALWAYS);
        HBox saturationSliderContainer = new HBox(10, saturationLabel, saturationSlider);

        Slider luminanceSlider = new Slider(0.7, 1.3, 1);
        luminanceSlider.setShowTickLabels(true);
        luminance.bind(luminanceSlider.valueProperty());
        luminanceSlider.valueProperty().addListener((obs, oldV, newV) -> {
            repaintDebounce.debounce();
        });
        Label luminanceLabel = new Label("Luminance");
        HBox.setHgrow(luminanceSlider, Priority.ALWAYS);
        HBox luminanceSliderContainer = new HBox(10, luminanceLabel, luminanceSlider);

        hueLabel.minWidthProperty().bind(luminanceLabel.widthProperty());
        saturationLabel.minWidthProperty().bind(luminanceLabel.widthProperty());

        group.selectToggle(redToggle);
        group.selectedToggleProperty().addListener((obs, oldV, newV) -> {
            saturationSlider.setValue(1);
            hueSlider.setValue(1);
            luminanceSlider.setValue(1);
        });

        HBox toggleButtons = new HBox(25, redToggle, orangeToggle, yellowToggle, greenToggle, aquaToggle, blueToggle, purpleToggle, magentaToggle);
        toggleButtons.getChildrenUnmodifiable().forEach(child -> {
            ((ToggleButton) child).setPrefWidth(128);
            HBox.setHgrow(child, Priority.ALWAYS);
        });
        VBox sliders = new VBox(15, toggleButtons, hueSliderContainer, saturationSliderContainer, luminanceSliderContainer);
        sliders.setPadding(new Insets(10, 10, 10, 10));
        BorderPane root = new BorderPane();
        root.setTop(sliders);
        root.setLeft(sourceCanvas);
        root.setRight(targetCanvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("HSL Image Editor");
        primaryStage.show();

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPEG Images", "*.jpg", "*.jpeg"));
        File imageFile = fileChooser.showOpenDialog(null);
        BufferedImage bufferedImage;
        if (imageFile == null) {
            bufferedImage = ImageIO.read(Main.class.getClassLoader().getResource("car.jpg"));
        } else {
            bufferedImage = ImageIO.read(imageFile);
        }
        this.target = AwtUtils.fromBufferedImage(bufferedImage);
        drawImage(target, sourceCanvas);
        drawImage(target, targetCanvas);
    }
}
