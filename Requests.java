public String GetToken(Boolean isCServer, String username, String password, String hostname, int port, String domain) {

		IsCServer = isCServer;
		_serverUrl = hostname;
		_username = username;
		_password = password;
		_port = port;
		_domain = domain;

		String responseString = null;

		try {
			final BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
			NTCredentials credentials = new NTCredentials(_username, _password.toCharArray(), _serverUrl + ":" + _port, _domain);
			credsProvider.setCredentials(new AuthScope(_serverUrl, port), credentials);
			String bodyAsString = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope " +
					"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
					"xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body>" +
					"<Login xmlns=\"http://videoos.net/2/XProtectCSServerCommand\"><instanceId>"
					+ _thisInstance + "</instanceId><currentToken></currentToken></Login></soap:Body></soap:Envelope>";
			String dataLength = String.valueOf(bodyAsString.length());

			HttpPost httpPost = new HttpPost("http://" + _serverUrl + ":" + _port + "/ServerAPI/ServerCommandService.asmx");
			httpPost.setEntity(new StringEntity(bodyAsString));
			httpPost.addHeader("SOAPAction", "http://videoos.net/2/XProtectCSServerCommand/Login");
			httpPost.addHeader("Content-Type", "text/xml");
			httpPost.addHeader("Connection", "keep-alive");
			httpPost.addHeader("Accept-Encoding", "gzip,deflate");
			httpPost.addHeader("Host", _serverUrl);

			CloseableHttpClient client = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();

			CloseableHttpResponse response = client.execute(httpPost);
			if (response.getCode() == 200) {
				responseString = EntityUtils.toString(response.getEntity());
				String FullToken = getFullNameFromXml(responseString, "Token");
				String[] split = FullToken.split("#");
				_token = split[1];
				return split[1];
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
