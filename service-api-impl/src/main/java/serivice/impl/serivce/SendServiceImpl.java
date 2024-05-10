package serivice.impl.serivce;


import com.common.pipeline.ProcessController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.api.pojo.BatchRequest;
import service.api.pojo.SendRequest;
import service.api.pojo.SendResponse;
import service.api.service.SendService;

@Service
public class SendServiceImpl implements SendService {

    @Autowired
    private ProcessController processController;


    @Override
    public SendResponse send(SendRequest request) {
        return null;
    }

    @Override
    public SendResponse batchSend(BatchRequest request) {
        return null;
    }
}
