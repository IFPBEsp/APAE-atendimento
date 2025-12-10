import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequestMapping
@RestController("/pressigned")
public class PressignedUrlController {
    PresignedUrlService presignedUrlService;
    public PressignedUrlController (PresignedUrlService presignedUrlService) {
        this.presignedUrlService = presignedUrlService;
    }


    @GetMapping
    public ResponseEntity<String> obterUrl (@PathVariable String bucket,
                                            @RequestParam(name = "objectName") String objectName) {
        String url = presignedUrlService.gerarUrlPreAssinada(bucket, objectName);
        return ResponseEntity.ok(url);
    }


}