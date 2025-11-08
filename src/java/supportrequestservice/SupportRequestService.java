package supportrequestservice;

import model.SupportRequest;
import supportrequestdao.ISupportRequestDAO;
import supportrequestdao.SupportRequestDAO;
import java.util.List;

/**
 * Service implementation cho SupportRequest
 */
public class SupportRequestService implements ISupportRequestService {
    
    private ISupportRequestDAO supportRequestDAO;
    
    public SupportRequestService() {
        this.supportRequestDAO = new SupportRequestDAO();
    }
    
    @Override
    public boolean createRequest(SupportRequest request) {
        // Validate input
        if (request.getUserID() <= 0) {
            System.err.println("Invalid userID: " + request.getUserID());
            return false;
        }
        
        if (request.getSubject() == null || request.getSubject().trim().isEmpty()) {
            System.err.println("Subject is required");
            return false;
        }
        
        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            System.err.println("Message is required");
            return false;
        }
        
        if (request.getStatus() == null || request.getStatus().trim().isEmpty()) {
            request.setStatus("Open");
        }
        
        int result = supportRequestDAO.insert(request);
        return result > 0;
    }
    
    @Override
    public SupportRequest getRequestById(int requestID) {
        if (requestID <= 0) {
            return null;
        }
        
        return supportRequestDAO.getById(requestID);
    }
    
    @Override
    public List<SupportRequest> getUserRequests(int userID) {
        if (userID <= 0) {
            return new java.util.ArrayList<>();
        }
        
        return supportRequestDAO.getByUserID(userID);
    }
    
    @Override
    public List<SupportRequest> getAllRequests() {
        return supportRequestDAO.getAll();
    }
    
    @Override
    public List<SupportRequest> getRequestsByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return new java.util.ArrayList<>();
        }
        
        return supportRequestDAO.getByStatus(status);
    }
    
    @Override
    public boolean updateRequest(SupportRequest request) {
        // Validate input
        if (request.getRequestID() <= 0) {
            return false;
        }
        
        if (request.getSubject() == null || request.getSubject().trim().isEmpty()) {
            System.err.println("Subject is required");
            return false;
        }
        
        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            System.err.println("Message is required");
            return false;
        }
        
        if (request.getStatus() == null || request.getStatus().trim().isEmpty()) {
            System.err.println("Status is required");
            return false;
        }
        
        return supportRequestDAO.update(request);
    }
    
    @Override
    public boolean deleteRequest(int requestID) {
        if (requestID <= 0) {
            return false;
        }
        
        return supportRequestDAO.delete(requestID);
    }
    
    @Override
    public int createRequest(int userID, String subject, String message) {
        if (userID <= 0) {
            return -1;
        }
        
        if (subject == null || subject.trim().isEmpty()) {
            return -1;
        }
        
        if (message == null || message.trim().isEmpty()) {
            return -1;
        }
        
        SupportRequest request = new SupportRequest();
        request.setUserID(userID);
        request.setSubject(subject.trim());
        request.setMessage(message.trim());
        request.setStatus("Open");
        
        boolean success = createRequest(request);
        return success ? request.getRequestID() : -1;
    }
    
    @Override
    public int countByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return 0;
        }
        
        List<SupportRequest> requests = getRequestsByStatus(status);
        return requests != null ? requests.size() : 0;
    }
    
    @Override
    public boolean updateStatus(int requestID, String status) {
        if (requestID <= 0) {
            return false;
        }
        
        if (status == null || status.trim().isEmpty()) {
            return false;
        }
        
        SupportRequest request = getRequestById(requestID);
        if (request == null) {
            return false;
        }
        
        request.setStatus(status.trim());
        return updateRequest(request);
    }
}

