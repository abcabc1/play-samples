package utils;

import net.coobird.thumbnailator.ThumbnailParameter;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ImageUtil {

    public static void thumbnailOfUrl(URL url, String suffix, double scale, int width, int height, String destinationDir) throws IOException {
        String urlPath = url.getPath();
        String query = url.getQuery();
        if (query != null) {
            urlPath = urlPath + query.substring(query.indexOf("=") + 1);
        }
        String path = destinationDir + urlPath.substring(0, urlPath.lastIndexOf("/"));
        String output = urlPath.substring(urlPath.lastIndexOf(".") + 1);
        System.out.println(urlPath);
        String fileName = urlPath.substring(urlPath.lastIndexOf("/"), urlPath.lastIndexOf(".")) + suffix + "." + output;
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        Thumbnails.of(url)
//                .scale(scale)
                .size(width, height)
                .outputFormat(output)
                .toFile(path + fileName);
    }

    public static void thumbnailOfUrls(URL[] urls, String suffix, double scale, int width, int height, String destinationDir) {
        for (URL url : urls) {
            try {
                thumbnailOfUrl(url, suffix, scale, width, height, destinationDir);
            } catch (IOException e) {
                System.out.print(url+e.getMessage());
            }
        }
    }

    @NotNull
    private static Rename getRename(String suffix) {
        return new Rename() {
            @Override
            public String apply(String name, ThumbnailParameter param) {
                return appendSuffix(name, suffix);
            }
        };
    }

    public static void thumbnailOfPaths(double scale, String... filePaths) throws IOException {
        Thumbnails.of(filePaths)
                .scale(scale)
                .toFiles(Rename.SUFFIX_DOT_THUMBNAIL);
    }

    public static void thumbnailOfPaths(String suffix, double scale, String... filePaths) throws IOException {
        Rename rename = getRename(suffix);
        Thumbnails.of(filePaths)
                .scale(scale)
                .toFiles(rename);
    }
}
