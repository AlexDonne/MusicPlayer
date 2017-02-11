/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecteuraudio.metier;

import com.github.axet.vget.VGet;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.axet.vget.info.VGetParser;
import com.github.axet.vget.info.VideoFileInfo;
import com.github.axet.vget.info.VideoInfo;
import com.github.axet.vget.vhs.YouTubeMPGParser;
import com.github.axet.wget.SpeedInfo;
import com.github.axet.wget.info.DownloadInfo.Part;
import com.github.axet.wget.info.DownloadInfo.Part.States;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
/**
 *
 * @author nahel
 */
public class ManagedDownload extends Thread {

    private final StringProperty downloadStatusProperty=new SimpleStringProperty();
    public StringProperty downloadStatusProperty(){return downloadStatusProperty; }
    public String getdownloadStatus() {return downloadStatusProperty.get();}
    public void setdownloadStatus(String status) {this.downloadStatusProperty.set(status);}

    private final StringProperty pathDownloadProperty=new SimpleStringProperty();
    public StringProperty pathDownloadProperty(){return pathDownloadProperty; }
    public String getpathDownload() {return pathDownloadProperty.get();}
    public void setpathDownload(String path) {this.pathDownloadProperty.set(path);}
    
    private final DoubleProperty progressProperty=new SimpleDoubleProperty();
    public DoubleProperty progressProperty(){return progressProperty; }
    public Double getProgress() {return progressProperty.get();}
    public void setProgress(Double value) {this.progressProperty.set(value);}
    
    private String url;
    private String dest;
    
    public ManagedDownload(String url, String dest){
        this.url=url;
        this.dest=dest;
    }
    
    class VGetStatus implements Runnable {
        VideoInfo videoinfo;
        long last;

        Map<VideoFileInfo, SpeedInfo> map = new HashMap<VideoFileInfo, SpeedInfo>();

        public VGetStatus(VideoInfo i) {
            this.videoinfo = i;
        }

        public SpeedInfo getSpeedInfo(VideoFileInfo dinfo) {
            SpeedInfo speedInfo = map.get(dinfo);
            if (speedInfo == null) {
                speedInfo = new SpeedInfo();
                speedInfo.start(dinfo.getCount());
                map.put(dinfo, speedInfo);
            }
            return speedInfo;
        }

        @Override
        public void run() {
            
             List<VideoFileInfo> dinfoList = videoinfo.getInfo();
             VideoFileInfo dinfo=dinfoList.get(0);
            // notify app or save download state
            // you can extract information from DownloadInfo info;
            switch (videoinfo.getState()) {
            case EXTRACTING:
            case EXTRACTING_DONE:
            case DONE:            
                break;
            case ERROR:
                break;
            case RETRYING:
                break;
            case DOWNLOADING:
                long now = System.currentTimeMillis();
                if (now - 1000 > last) {
                    last = now;

                    String parts = "";


                        SpeedInfo speedInfo = getSpeedInfo(dinfo);
                        speedInfo.step(dinfo.getCount());

                        List<Part> pp = dinfo.getParts();
                        if (pp != null) {
                            // multipart download
                            for (Part p : pp) {
                                if (p.getState().equals(States.DOWNLOADING)) {
                                    parts += String.format("part#%d(%.2f) ", p.getNumber(),
                                            p.getCount() / (float) p.getLength());
                                }
                            }
                        }
                        //String s=String.format("Téléchargement de \"%s\": %.2f %s (%s)", dinfo.toString(),(dinfo.getCount() / (float) dinfo.getLength())*100, parts,
                                //Utils.formatSpeed(speedInfo.getCurrentSpeed()));
                        String s=String.format("Téléchargement : %.2f%s %s (%s)",(dinfo.getCount() / (float) dinfo.getLength())*100, "%", parts,
                                Utils.formatSpeed(speedInfo.getCurrentSpeed()));
                        
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                setdownloadStatus(s);
                                double d=dinfo.getCount() / (float) dinfo.getLength();
                                setProgress(d);
                            }
                        });
                    
                    
                    
                    
                }
                break;
            default:
                break;
            }
            
        }
    }

    @Override
    public void run() {
        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                setdownloadStatus("Lancement du téléchargement...");
                            }
                        });
        String s=download(url, dest);
        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                setpathDownload(s);
                            }
                        });
    }
    public String download(String url, String dest) {

        // ex: /Users/axet/Downloads/
        File path = new File(dest);

        try {
            final AtomicBoolean stop = new AtomicBoolean(false);

            URL web = new URL(url);

            // [OPTIONAL] limit maximum quality, or do not call this function if
            // you wish maximum quality available.
            //
            // if youtube does not have video with requested quality, program
            // will raise en exception.
            VGetParser user = null;

            // create proper html parser depends on url
            VGet.parser(web);

            // download limited video quality from youtube
            //user = new YouTubeQParser(YoutubeQuality.p480);

            // download mp4 format only, fail if non exist
            user = new YouTubeMPGParser();

            // create proper videoinfo to keep specific video information
            VideoInfo videoinfo = user.info(web);

            VGet v = new VGet(videoinfo, path);

            VGetStatus notify = new VGetStatus(videoinfo);

            // [OPTIONAL] call v.extract() only if you d like to get video title
            // or download url link before start download. or just skip it.
            v.extract(user, stop, notify);

            v.download(user, stop, notify);
            Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                setdownloadStatus("Téléchargement terminé !");
                            }
                        });
            
                  return dest + "/" + videoinfo.getTitle() + ".mp4";
        } 
        catch (Exception e) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText(null);
                    alert.setContentText("Vidéo introuvable, la page n'est pas un lien d'une vidéo YouTube, ou la vidéo est protégé.");
                    alert.showAndWait();
                    setdownloadStatus("Téléchargement annulé");
                }
            });
            
            if(getpathDownload()==null){
                return "";
            }
            return null;
        }
    }

}
