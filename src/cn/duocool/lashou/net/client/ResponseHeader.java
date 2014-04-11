package cn.duocool.lashou.net.client;

public class ResponseHeader {
	
	// ÿ����Ӧ�����õ�
	private String status;
	
	// ��Ӧ����
	private String method;
	
	// ��Ӧ��Ϣ
	private String message;
		
	// ��Ӧ��Ϣ
	private String body;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
