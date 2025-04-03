package Controllers;

import Router.Request;
import Router.RequestMethod;
import Router.RouteResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

public class FileUploadController {
    public RouteResult handleFileUpload(RequestMethod method, Request request) {
        String uploadDir = "path/to/upload/dir/"; // 设置上传目录
        InputStream inputStream = request.getFileInputStream("file");
        String fileName = request.getParam("fileName");

        if (inputStream == null || fileName == null) {
            return new RouteResult(Router.HttpStatus.BAD_REQUEST, Collections.singletonMap("message", "缺少文件或文件名"));
        }

        FileUploadHandler fileUploadHandler = new FileUploadHandler(uploadDir);
        if (fileUploadHandler.handleFileUpload(inputStream, fileName)) {
            return new RouteResult(Router.HttpStatus.OK, Collections.singletonMap("message", "文件上传成功"));
        } else {
            return new RouteResult(Router.HttpStatus.INTERNAL_SERVER_ERROR, Collections.singletonMap("message", "文件上传失败"));
        }
    }

    private static class FileUploadHandler {
        private String uploadDir;

        public FileUploadHandler(String uploadDir) {
            this.uploadDir = uploadDir;
        }

        public boolean handleFileUpload(InputStream inputStream, String fileName) {
            File file = new File(uploadDir, fileName);
            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, length);
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}