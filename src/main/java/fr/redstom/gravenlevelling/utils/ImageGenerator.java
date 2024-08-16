package fr.redstom.gravenlevelling.utils;

import fr.redstom.gravenlevelling.jda.entities.GravenMember;
import fr.redstom.gravenlevelling.jda.services.GravenMemberService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Member;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ImageGenerator {

    public static final DecimalFormat RANK_FORMATTER = (DecimalFormat) NumberFormat.getInstance(Locale.US);

    public static final Font OUTFIT;

    public static final Color PRIMARY_BG = new Color(0x242220);
    public static final Color SECONDARY_BG = new Color(0x3f3c38);

    public static final Color TEXT = new Color(0xeeeeee);
    public static final Color TEXT_ACCENT = new Color(0x898887);

    public static final Color ACCENT = new Color(0xff9142);

    public static final Image PODIUM;
    public static final Image PODIUM_BRONZE;
    public static final Image PODIUM_SILVER;
    public static final Image PODIUM_GOLD;
    public static final Image STAR;
    public static final Image TROPHEE;

    private final LevelUtils levelUtils;
    private final GravenMemberService memberService;

    static {
        DecimalFormatSymbols decimalFormatSymbols = RANK_FORMATTER.getDecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator(' ');
        RANK_FORMATTER.setDecimalFormatSymbols(decimalFormatSymbols);

        try {
            OUTFIT = Font.createFont(Font.TRUETYPE_FONT, ImageGenerator.class.getClassLoader().getResourceAsStream("fonts/outfit.ttf"));

            PODIUM = ImageIO.read(ImageGenerator.class.getClassLoader().getResourceAsStream("images/podium.png"));
            PODIUM_BRONZE = ImageIO.read(ImageGenerator.class.getClassLoader().getResourceAsStream("images/podium-bronze.png"));
            PODIUM_SILVER = ImageIO.read(ImageGenerator.class.getClassLoader().getResourceAsStream("images/podium-silver.png"));
            PODIUM_GOLD = ImageIO.read(ImageGenerator.class.getClassLoader().getResourceAsStream("images/podium-gold.png"));
            STAR = ImageIO.read(ImageGenerator.class.getClassLoader().getResourceAsStream("images/star.png"));
            TROPHEE = ImageIO.read(ImageGenerator.class.getClassLoader().getResourceAsStream("images/trophee.png"));
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public BufferedImage generateLevelImage(Member member, GravenMember gMember) {
        Color accent = member.getColor() == null ? ACCENT : member.getColor();

        InputStream avatar = member.getEffectiveAvatar().download(512).join();

        BufferedImage image = new BufferedImage(1600, 400, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);


        g2d.setColor(PRIMARY_BG);
        g2d.fillRect(0, 0, 1600, 400);

        g2d.setColor(accent);
        g2d.fillRect(0, 0, 350, 350);

        BufferedImage avatarAsImage = ImageIO.read(avatar);
        g2d.drawImage(avatarAsImage, 10, 10, 330, 330, null);

        g2d.setColor(SECONDARY_BG);
        g2d.setBackground(SECONDARY_BG);
        g2d.fillRect(0, 350, 1600, 50);

        double advancement = (double) gMember.experience() / (double) levelUtils.xpForNextLevelAt(gMember.level());
        g2d.setColor(accent);
        g2d.setBackground(accent);
        g2d.fillRect(0, 350, (int) (advancement * 1600), 50);

        String text = STR."@\{member.getUser().getName()}";

        Font actualFont = OUTFIT.deriveFont(Font.BOLD, 72f);
        FontMetrics metrics = g2d.getFontMetrics(actualFont);

        int w = metrics.stringWidth(text);

        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(TEXT);
        g2d.setBackground(TEXT);
        g2d.setFont(actualFont);
        g2d.drawString(text, 400, 100);

        g2d.setColor(accent);
        g2d.drawLine(400, 125, 400 + w, 125);
        g2d.setColor(TEXT);

        Font contentFont = OUTFIT.deriveFont(Font.BOLD, 56f);
        FontMetrics contentMetrics = g2d.getFontMetrics(contentFont);
        Font titleFont = OUTFIT.deriveFont(Font.PLAIN, 36f);
        FontMetrics titleMetrics = g2d.getFontMetrics(titleFont);

        int startingWidth = 420;
        int imageSize = 96;
        int margin = 25;

        g2d.drawImage(STAR, startingWidth, 177, imageSize, imageSize, null);
        String levelContent = STR."\{gMember.level()}";
        String levelTitle = "Niveau :";
        int levelWidth = Math.max(contentMetrics.stringWidth(levelContent), titleMetrics.stringWidth(levelTitle));

        int levelTextX = startingWidth + imageSize + margin;
        g2d.setFont(contentFont);
        g2d.drawString(levelContent, levelTextX, 213 + 60);

        g2d.setFont(titleFont);
        g2d.drawString(levelTitle, levelTextX, 177 + 36);


        g2d.drawImage(TROPHEE, startingWidth + levelWidth + imageSize + margin * 3, 177, imageSize, imageSize, null);
        String experienceContent = STR."\{levelUtils.formatExperience(gMember.experience(), gMember.level())}";
        String experienceTitle = "Expérience :";
        int experienceWidth = Math.max(contentMetrics.stringWidth(experienceContent), titleMetrics.stringWidth(experienceTitle));

        int experienceTextX = startingWidth + levelWidth + imageSize * 2 + margin * 4;
        g2d.setFont(contentFont);
        g2d.drawString(experienceContent, experienceTextX, 213 + 60);

        g2d.setFont(titleFont);
        g2d.drawString(experienceTitle, experienceTextX, 177 + 36);

        int rank = memberService.getRank(member);
        g2d.drawImage(switch(rank) {
            case 1 -> PODIUM_GOLD;
            case 2 -> PODIUM_SILVER;
            case 3 -> PODIUM_BRONZE;
            default -> PODIUM;
        }, startingWidth + levelWidth + experienceWidth + imageSize * 2 + margin * 6, 177, imageSize, imageSize, null);

        int podiumTextX = startingWidth + levelWidth + experienceWidth + imageSize * 3 + margin * 7;
        g2d.setFont(contentFont);
        g2d.drawString(RANK_FORMATTER.format(rank), podiumTextX, 213 + 60);

        g2d.setFont(titleFont);
        g2d.drawString("Rang :", podiumTextX, 177 + 36);

        return image;
    }

}
