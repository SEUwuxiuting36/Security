package processing;

/**
 * @author: Wu Xiuting
 */
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import org.apache.commons.codec.binary.Base64;

public class Main extends JFrame {

	private static final long serialVersionUID = 1L;
	private static JPanel contentPane;
	public static String symAlgs = "DES";
	public static String shaAlgs = "SHA-1";
	public static String keySelect = "generate";
	public static String symKey = "";
	public static String sk = "";
	static String aesKey;
	static String desKey;
	static SecretKey secretKey;
	
	public String publicKeyA="";
	public String publicKeyB="";
	public String privateKeyA="";
	public String privateKeyB="";
	static String setKey = "";
	public String symStr = "";
	
	static StringBuilder sendInfo;
	static StringBuilder receInfo;
	static String plainText;
	static String am;
	static String bm;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
					frame.setTitle("�ӽ���ͨ��ϵͳ");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	//��ɢ��ֵ
	public String calSha(String hashInput) throws Exception {
		String hash = new String();
		if (shaAlgs == "SHA-1") {
		hash = SHA.shaHash(hashInput);
		}
		else hash = MD5.getMD5Hash(hashInput);
		return hash;
	}
	
	//�õ�A�Ĺ�˽Կ��
	static Map<Integer, String> keyAMap = new HashMap<Integer, String>();  //���ڷ�װ��������Ĺ�Կ��˽Կ
	public static void aKeyPair() throws NoSuchAlgorithmException {  
		// KeyPairGenerator���������ɹ�Կ��˽Կ�ԣ�����RSA�㷨���ɶ���  
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");  
		// ��ʼ����Կ������������Կ��СΪ96-1024λ  
		keyPairGen.initialize(512,new SecureRandom());  
		// ����һ����Կ�ԣ�������keyPair��  
		KeyPair keyPair = keyPairGen.generateKeyPair();  
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // �õ�˽Կ  
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // �õ���Կ  
		String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));  
		// �õ�˽Կ�ַ���  
		String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded())));  
		// ����Կ��˽Կ���浽Map
		keyAMap.put(0,publicKeyString);  //0��ʾ˽Կ
		keyAMap.put(1,privateKeyString);  //1��ʾ��Կ
		
	}  
	
	//�õ�B�Ĺ�˽Կ��
	static Map<Integer, String> keyBMap = new HashMap<Integer, String>();  //���ڷ�װ��������Ĺ�Կ��˽Կ
	public static void bKeyPair() throws NoSuchAlgorithmException {  
		// KeyPairGenerator���������ɹ�Կ��˽Կ�ԣ�����RSA�㷨���ɶ���  
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");  
		// ��ʼ����Կ������������Կ��СΪ96-1024λ  
		keyPairGen.initialize(512,new SecureRandom());  
		// ����һ����Կ�ԣ�������keyPair��  
		KeyPair keyPair = keyPairGen.generateKeyPair();  
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // �õ�˽Կ  
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // �õ���Կ  
		String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));  
		// �õ�˽Կ�ַ���  
		String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded())));  
		// ����Կ��˽Կ���浽Map
		keyBMap.put(0,publicKeyString);  //0��ʾ˽Կ	
		keyBMap.put(1,privateKeyString);  //1��ʾ��Կ			
	}  
	
	//��A��˽Կ�������ĵ�ɢ��ֵ
	public String prEncry(String prInput) throws Exception {
	    //˽Կ���ܵ��ַ���
		aKeyPair();
		String prOutput = RSA.encrypt(prInput,keyAMap.get(0));
		return prOutput;
	}
	
	 public String getSymKey() throws NoSuchAlgorithmException {
		  if (setKey != "") {
			 symKey =  DES.getKeyByPass(setKey);
		  }
		  else symKey = DES.ranKey();
		  return symKey;
	 }
	
	 public void initKey() throws NoSuchAlgorithmException {
		//��������������˽Կ��
		bKeyPair();
		aKeyPair();
		//��������һ��Գ���Կ
		sk = getSymKey();
			
	 }
	
	 
	public SecretKey getSecretKey(String sk) {
		  secretKey = new SecretKeySpec(DES.hexStringToBytes(sk), "DES");
		  return secretKey;	  
	  }
	
	//A:�öԳ���Կ(ECB����ģʽ)�������ĺ�ǩ�����ɢ��ֵ
	public String sendMessage(String str1,String str2,SecretKey sk) throws Exception {
		String str = str1+str2;
		if (symAlgs == "DES") {
			String encry = DES.encrypt(str,secretKey);
			return encry;
		}
		else if (symAlgs == "AES") {
			if (setKey != "") {
				aesKey = AES.getKeyByPass(setKey);
			}
			else {
				aesKey = AES.ranKey();
			}
			String encry = AES.encrypt(str,aesKey);
			return encry;
		}
		return null;
	}
	
	//A�� ��B�Ĺ�Կ���ܶԳ���Կ
	public String keyEncry(String str) throws Exception {
		bKeyPair();
		String eKey = RSA.encrypt(str,keyBMap.get(0));
		return eKey;
	}
	
	//B�� ��B�Ĺ�˽Կ���ܶԳ���Կ
	public String keyDecry(String str) throws Exception {
		String dKey = RSA.decrypt(str,keyBMap.get(1));
		return dKey;
	}
	
	//B:�öԳ���Կ������Ϣ,�õ�����M������ǩ�����ɢ��ֵ
	public String getMessage(String str,SecretKey secretKey3) throws Exception {
		if (symAlgs == "DES") {
			System.out.println("���ܵ�DES��Կ��"+symKey);
			String decry = DES.decrypt(str,secretKey3);
			return decry;
		}
		else if (symAlgs == "AES") {
			String decry = AES.decrypt(str,aesKey);
			return decry;
		}
		return null;
	}
	
	public String receHash(String input) throws Exception {
		int lengthM = plainText.length();
		int lengthInput = input.length();
		String reHash = input.substring(lengthM,lengthInput);
		return reHash;
	}
	
	//��A�Ĺ�Կ��H��M�����н���
	public String puDecry(String prInput) throws Exception {
	    //˽Կ���ܵ��ַ���
		String puOutput = RSA.decrypt(prInput,keyAMap.get(1));
		return puOutput;
	}
	
	//B:�Խ��յ������ļ�����ɢ��ֵ
	public String mHash(String rInput) throws Exception {
		String rhash = new String();
		String reM = rInput.substring(0,plainText.length());
		if (shaAlgs == "SHA-1") {
		rhash = SHA.shaHash(reM);
		}
		else rhash = MD5.getMD5Hash(reM);
		return rhash;
	}
	
	//���г���
	public void run(String plainText) throws Exception {
		am = "���ͷ�A��˽ԿRKa��" + keyAMap.get(0)+ "\n";
		am = am + "���ͷ�A�Ĺ�ԿUKa��" + keyAMap.get(1)+ "\n";
		am = am + "�Գ���ԿK��" + symKey+ "\n";
		String hashEncry = calSha(plainText);
		am = am + "����M��ɢ��ֵH��M����" + hashEncry+ "\n";
		String prOut = prEncry(hashEncry);
		am = am + "PKa���ܺ��H��M����" + prOut+ "\n";
		SecretKey sKey = getSecretKey(sk);
		String eKey = keyEncry(sk);
		String dKey = keyDecry(eKey);
		String sendStr = sendMessage(plainText, prOut,sKey);
		am = am + "�Գ���ԿK���ܺ��M��ǩ��H��M����" + sendStr+ "\n";
		am = am + "UKb���ܺ�ĶԳ���Կ��" + eKey;
		System.out.println(am + "\n");
		
		bm = "���ͷ�B��˽ԿRKb��" + keyBMap.get(0)+ "\n";
		bm = bm + "���ͷ�B�Ĺ�ԿUKb��" + keyBMap.get(1)+ "\n";
		bm = bm + "RKb���ܺ�ĶԳ���ԿK'��" + dKey+ "\n";
		String receStr = getMessage(sendStr,sKey);
		bm = bm + "�Գ���ԿK'���ܵó���M'��" + receStr.substring(0,plainText.length())+ "\n";
		String receHash = receHash(receStr);
		bm = bm + "�Գ���ԿK'���ܵó���ǩ��H��M'����" + receHash+ "\n";
		String puDecry = puDecry(receHash);
		bm = bm + "UKa���ܵó���H��M'����" + puDecry+ "\n";
		String reHash = mHash(receStr);
		bm = bm + "����M'��ɢ��ֵH��M''����" + reHash+ "\n";
		if(reHash.equals(puDecry)) {
			bm = bm + "�Ƚ�H��M'����H��M''�������ܳɹ�";
		}
		else bm = bm + "�Ƚ�H��M'����H��M''��������ʧ��";
		System.out.println(bm + "\n");
	}

	/**
	 * Create the frame.
	 */
	public Main() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1000, 810);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(10, 5, 200, 590);
		panel_1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel_1.setLayout(null);
		contentPane.add(panel_1);
		
		JLabel label = new JLabel("����");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("΢���ź�", Font.PLAIN, 30));
		label.setBounds(70, 5, 60, 40);
		panel_1.add(label);
		
		JLabel lblHash = new JLabel("Hash�㷨��");
		lblHash.setFont(new Font("΢���ź�", Font.PLAIN, 25));
		lblHash.setBounds(10, 53, 135, 34);
		panel_1.add(lblHash);
		
		ButtonGroup hashGroup = new ButtonGroup();
		JRadioButton shaButton = new JRadioButton("SHA",true);
		shaButton.setFont(new Font("Tahoma", Font.PLAIN, 25));
		shaButton.setBounds(10, 93, 79, 35);
		JRadioButton md5Button = new JRadioButton("MD5",false);
		md5Button.setFont(new Font("Tahoma", Font.PLAIN, 25));
		md5Button.setBounds(10, 130, 83, 35);
		hashGroup.add(shaButton);
		hashGroup.add(md5Button);
		panel_1.add(shaButton);
		panel_1.add(md5Button);
		
		//Ϊsha������Ӧ
		shaButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				shaAlgs = "SHA-1";
			}
		});
		//Ϊmd5������Ӧ
		md5Button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				shaAlgs = "MD5";
			}
		});
		
		JLabel lblNewLabel = new JLabel("�ԳƼ����㷨��");
		lblNewLabel.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel.setFont(new Font("΢���ź�", Font.PLAIN, 25));
		lblNewLabel.setBounds(10, 180, 175, 34);
		panel_1.add(lblNewLabel);
		
		ButtonGroup symmetricGroup = new ButtonGroup();
		JRadioButton desButton = new JRadioButton("DES",true);
		desButton.setFont(new Font("Tahoma", Font.PLAIN, 25));
		desButton.setBounds(10, 220, 77, 35);
		JRadioButton aesButton = new JRadioButton("AES",false);
		aesButton.setFont(new Font("Tahoma", Font.PLAIN, 25));
		aesButton.setBounds(10, 257, 75, 35);
		symmetricGroup.add(desButton);
		symmetricGroup.add(aesButton);
		panel_1.add(desButton);
		panel_1.add(aesButton);
		
		//����DESѡ�����Ӧ�¼�
		desButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				 symAlgs = "DES";
			}
		});
		//����AESѡ�����Ӧ�¼�
		aesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				symAlgs = "AES";
			}
		});

		JLabel keyGenLabel = new JLabel("�Գ���Կ��Դ:");
		keyGenLabel.setHorizontalAlignment(SwingConstants.LEFT);
		keyGenLabel.setFont(new Font("΢���ź�", Font.PLAIN, 25));
		keyGenLabel.setBounds(10, 308, 156, 35);
		panel_1.add(keyGenLabel);
		
		ButtonGroup keyGenGroup = new ButtonGroup();
		JRadioButton autoGenButton = new JRadioButton("�Զ�����",true);
		autoGenButton.setFont(new Font("΢���ź�", Font.PLAIN, 22));
		autoGenButton.setBounds(10, 350, 121, 35);
		JRadioButton presetButton = new JRadioButton("�ı�������",true);
		presetButton.setFont(new Font("΢���ź�", Font.PLAIN, 22));
		presetButton.setBounds(10, 390, 143, 35);
		keyGenGroup.add(autoGenButton);
		keyGenGroup.add(presetButton);
		panel_1.add(autoGenButton);
		panel_1.add(presetButton);
		
		JTextArea textArea = new JTextArea(3,14);
		textArea.setBounds(10, 430, 183, 94);
		textArea.setFont(new Font("΢���ź�", Font.PLAIN, 22));
		textArea.setLineWrap(true);
		panel_1.add(textArea);
		//���ı�����ӹ�������
		JScrollPane textScrollPane = new JScrollPane(textArea);
		textScrollPane.setBounds(10, 430, 183, 94);
		panel_1.add(textScrollPane);
		
		//�Ƚ������õ�˵��
		JLabel sm = new JLabel("�����Ƚ���");
		sm.setHorizontalAlignment(SwingConstants.LEFT);
		sm.setFont(new Font("΢���ź�", Font.PLAIN, 25));
		sm.setBounds(40, 620, 183, 35);
		contentPane.add(sm);
		JLabel sm2 = new JLabel("���õ�ѡ��");
		sm2.setHorizontalAlignment(SwingConstants.LEFT);
		sm2.setFont(new Font("΢���ź�", Font.PLAIN, 25));
		sm2.setBounds(40, 660, 183, 35);
		contentPane.add(sm2);

		//�Զ����ɰ�ť��Ӧ
		autoGenButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setKey = "";
				textArea.setText("");
				textArea.setEnabled(false);
			}
		});
		//���밴ť��Ӧ
		presetButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setKey = textArea.getText();
				textArea.setEnabled(true);
			}
		});
				
		
		JButton btnNewButton = new JButton("ȷ��");
		btnNewButton.setFont(new Font("΢���ź�", Font.PLAIN, 22));
		btnNewButton.setBounds(10, 540, 78, 39);
		panel_1.add(btnNewButton);
		
		JButton changeButton = new JButton("�޸�");
		changeButton.setFont(new Font("΢���ź�", Font.PLAIN, 22));
		changeButton.setBounds(115, 540, 78, 39);
		panel_1.add(changeButton);
		
		//����������ʾ
		JLabel lblNewLabel_1 = new JLabel("���������ģ�");
		lblNewLabel_1.setFont(new Font("΢���ź�", Font.PLAIN, 25));
		lblNewLabel_1.setBounds(240, 15, 150, 30);
		contentPane.add(lblNewLabel_1);
		
		JTextArea mTextArea = new JTextArea();
		mTextArea.setBounds(390, 13, 550, 40);
		mTextArea.setFont(new Font("΢���ź�", Font.PLAIN, 20));
		mTextArea.setLineWrap(true);
		JScrollPane mScrollPane = new JScrollPane(mTextArea);
		mScrollPane.setBounds(390, 13, 550, 40);
		contentPane.add(mScrollPane);
		
		//���ͷ���ʾ���
		JPanel panel = new JPanel();
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel.setBounds(223, 120, 750, 290);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JLabel lblNewLabel_2 = new JLabel("\u53D1\u9001\u65B9");
		lblNewLabel_2.setFont(new Font("΢���ź�", Font.PLAIN, 25));
		lblNewLabel_2.setBounds(325, 8, 81, 35);
		panel.add(lblNewLabel_2);
		
		JTextArea textArea_send = new JTextArea();
		textArea_send.setBounds(8, 46, 735, 250);
		textArea_send.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		textArea_send.setFont(new Font("΢���ź�", Font.PLAIN, 20));
		textArea_send.setLineWrap(true);
		textArea_send.setEnabled(false);
		panel.add(textArea_send);
		JScrollPane sendScrollPane = new JScrollPane(textArea_send);
		sendScrollPane.setBounds(8, 46, 735, 250);
		panel.add(sendScrollPane);
		
		//���շ���ʾ���
		JPanel rPanel = new JPanel();
		rPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		rPanel.setBounds(223, 443, 750, 290);
		contentPane.add(rPanel);
		rPanel.setLayout(null);
		
		JLabel lblNewLabel_r = new JLabel("\u63A5\u6536\u65B9");
		lblNewLabel_r.setFont(new Font("΢���ź�", Font.PLAIN, 25));
		lblNewLabel_r.setBounds(325, 8, 81, 35);
		rPanel.add(lblNewLabel_r);
		
		JTextArea textArea_r = new JTextArea();
		textArea_r.setBounds(8, 46, 735, 250);
		textArea_r.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		textArea_r.setFont(new Font("΢���ź�", Font.PLAIN, 20));
		textArea_r.setLineWrap(true);
		textArea_r.setEnabled(false);
		rPanel.add(textArea_r);
		JScrollPane rScrollPane = new JScrollPane(textArea_r);
		rScrollPane.setBounds(8, 46, 735, 250);
		rPanel.add(rScrollPane);
		
		//�������ĺ�Ŀ�ʼ���Ͱ�ť
		JButton btnNewButton_1 = new JButton("\u5F00\u59CB\u4F20\u9001");
		btnNewButton_1.setFont(new Font("΢���ź�", Font.PLAIN, 22));
		btnNewButton_1.setBounds(750, 70, 123, 35);
		contentPane.add(btnNewButton_1);

	//��������ȷ����ť�¼�����
	btnNewButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			//TODO ����Ƿ�����Կ���룬���û��Ҫ��ʾ
			desButton.setEnabled(false);
			aesButton.setEnabled(false);
			shaButton.setEnabled(false);
			md5Button.setEnabled(false);
			autoGenButton.setEnabled(false);
			presetButton.setEnabled(false);
			textArea.setEnabled(false);
			btnNewButton.setEnabled(false);
			try {
				initKey();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			btnNewButton_1.setEnabled(true);
			mTextArea.setEnabled(true);
		}
	});

	changeButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			desButton.setEnabled(true);
			aesButton.setEnabled(true);
			shaButton.setEnabled(true);
			md5Button.setEnabled(true);
			autoGenButton.setEnabled(true);
			presetButton.setEnabled(true);
			if (!textArea.isEnabled())
			{
				textArea.setEnabled(true);
			}
			btnNewButton.setEnabled(true);
			mTextArea.setEnabled(false);
			btnNewButton_1.setEnabled(false);
			//��ռ�������
			mTextArea.setText("");
			//��շ�������
			textArea_send.setText("");
			//��ս�������
			textArea_r.setText("");
			changeButton.setEnabled(false);
		}
	});
	
	//��ʼ���Ͱ�ť�¼�����
	btnNewButton_1.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO ����Ƿ����������룬���û��Ҫ����
			plainText = mTextArea.getText();
			try {
				run(plainText);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			textArea_send.append(am);
			textArea_r.append(bm);
			changeButton.setEnabled(true);
		}
	});
}

}
