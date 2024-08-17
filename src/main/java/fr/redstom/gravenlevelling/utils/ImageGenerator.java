package fr.redstom.gravenlevelling.utils;

import fr.redstom.gravenlevelling.jda.entities.GravenMember;
import fr.redstom.gravenlevelling.jda.services.GravenMemberService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Member;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

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

        g2d.setClip(new Ellipse2D.Double(1490, 10, 100, 100));

        g2d.setColor(SECONDARY_BG);
        g2d.fillRect(1490, 10, 100, 100);

        if (member.getGuild().getIcon() != null) {
            InputStream serverLogo = member.getGuild().getIcon().download(512).join();
            BufferedImage serverLogoAsImage = ImageIO.read(serverLogo);

            g2d.drawImage(serverLogoAsImage, 1490, 10, 100, 100, null);
        } else {
            g2d.setFont(OUTFIT.deriveFont(24f));
            FontMetrics abbreviationMetrics = g2d.getFontMetrics();
            String serverAbbreviation = Arrays.stream(member.getGuild().getName().split(" ")).reduce("", (a, b) -> a + b.charAt(0));
            int serverAbbreviationWidth = abbreviationMetrics.stringWidth(serverAbbreviation);

            g2d.setColor(TEXT);
            g2d.setBackground(TEXT);
            g2d.drawString(serverAbbreviation, 1540 - serverAbbreviationWidth / 2, 60 - abbreviationMetrics.getHeight() / 2 + abbreviationMetrics.getAscent());
        }
        g2d.setClip(null);

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
        String rankText = RANK_FORMATTER.format(rank);
        if (gMember.experience() == 0 && gMember.level() == 0) {
            rank = -1;
            rankText = "-";
        }

        g2d.drawImage(switch (rank) {
            case 1 -> PODIUM_GOLD;
            case 2 -> PODIUM_SILVER;
            case 3 -> PODIUM_BRONZE;
            default -> PODIUM;
        }, startingWidth + levelWidth + experienceWidth + imageSize * 2 + margin * 6, 177, imageSize, imageSize, null);

        int podiumTextX = startingWidth + levelWidth + experienceWidth + imageSize * 3 + margin * 7;
        g2d.setFont(contentFont);
        g2d.drawString(rankText, podiumTextX, 213 + 60);

        g2d.setFont(titleFont);
        g2d.drawString("Rang :", podiumTextX, 177 + 36);

        return image;
    }


    @SneakyThrows
    public BufferedImage generateLeaderboardImage(int page, List<GravenMember> members, Function<GravenMember, Member> memberMapper) {
        List<Member> dMembers = members.stream().map(memberMapper).toList();

        int imageHeight = 185 + members.size() * 125 + (members.size() - 1) * 10;

        BufferedImage image = new BufferedImage(1175, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        g2d.setColor(SECONDARY_BG);
        g2d.fillRect(0, 0, 1175, 1525);

        g2d.setColor(PRIMARY_BG);
        g2d.fillRect(150, 25, 1000, 125);

        g2d.setFont(OUTFIT.deriveFont(40f));
        g2d.setColor(TEXT);
        g2d.setBackground(TEXT);

        drawCenteredText(g2d, "Rang", 222.5f, 87.5f);
        drawVCenteredText(g2d, "Nom d'utilisateur", 332.5f, 87.5f);
        drawCenteredText(g2d, "Niv.", 1088, 87.5f);

        g2d.setFont(OUTFIT.deriveFont(48f));
        for (int i = 0; i < members.size(); i++) {
            Member discordMember = dMembers.get(i);
            GravenMember gravenMember = members.get(i);

            Image avatar = ImageIO.read(discordMember.getEffectiveAvatar().download().join());

            drawMemberPosition(g2d, 25 + (i + 1) * 135, discordMember.getColor(), avatar, (page - 1) * 10 + i + 1, gravenMember.level(), discordMember.getUser().getName());
        }

        g2d.setColor(SECONDARY_BG);
        for (Integer x : List.of(150, 285, 1015)) {
            g2d.fillRect(x, 0, 10, 1675);
        }

        return image;
    }

    private void drawMemberPosition(Graphics2D g2d, int y, Color color, Image avatar, int rank, long level, String name) {
        g2d.setColor(PRIMARY_BG);
        g2d.fillRect(25, y, 1125, 125);

        g2d.setColor(color);
        g2d.fillRect(25, y, 125, 125);

        g2d.drawImage(avatar, 30, y + 5, 115, 115, null);

        g2d.setColor(TEXT);
        switch (rank) {
            case 1, 2, 3 -> g2d.drawImage(switch (rank) {
                case 1 -> PODIUM_GOLD;
                case 2 -> PODIUM_SILVER;
                case 3 -> PODIUM_BRONZE;
                default -> throw new IllegalStateException("Unexpected value: " + rank);
            }, 185, y + 25, 75, 75, null);
            default -> drawCenteredText(g2d, STR."#\{NumberUtils.formatNumber(rank)}", 222.5f, y + 62.5f);
        }
        drawVCenteredText(g2d, STR."@\{name}", 332.5f, y + 62.5f);

        drawCenteredText(g2d, NumberUtils.formatNumber(level), 1088, y + 62.5f);
    }

    private void drawCenteredText(Graphics2D g2d, String text, float x, float y) {
        FontMetrics metrics = g2d.getFontMetrics();
        g2d.drawString(text, x - metrics.stringWidth(text) / 2f, y - metrics.getHeight() / 2f + metrics.getAscent());
    }

    private void drawVCenteredText(Graphics2D g2d, String text, float x, float y) {
        FontMetrics metrics = g2d.getFontMetrics();
        g2d.drawString(text, x, y - metrics.getHeight() / 2f + metrics.getAscent());
    }
}
