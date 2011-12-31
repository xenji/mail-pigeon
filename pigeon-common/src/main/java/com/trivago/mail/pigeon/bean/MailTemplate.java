package com.trivago.mail.pigeon.bean;


import com.trivago.mail.pigeon.storage.ConnectionFactory;
import com.trivago.mail.pigeon.storage.IndexTypes;
import com.trivago.mail.pigeon.storage.RelationTypes;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.server.rest.web.DatabaseActions;

public class MailTemplate extends AbstractBean
{
	public static final String ID = "template_id";
	public static final String SUBJECT = "subject";
	public static final String TEXT = "text_content";
	public static final String HTML = "html_content";
	public static final String TITLE = "title";


	public MailTemplate(final Node underlayingNode)
	{
		this.dataNode = underlayingNode;
	}

	public MailTemplate(final long templateId)
	{
		dataNode = ConnectionFactory.getTemplateIndex().get(IndexTypes.TEMPLATE_ID, templateId).getSingle();
	}

	public MailTemplate(final long templateId, final String title, final String text, final String html, final String subject)
	{
		Transaction tx = ConnectionFactory.getDatabase().beginTx();
		try
		{
			dataNode = ConnectionFactory.getDatabase().createNode();
			writeProperty(ID, templateId);
			writeProperty("type", getClass().getName());
			writeProperty(SUBJECT, subject);
			writeProperty(TEXT, text);
			writeProperty(HTML, html);
			writeProperty(TITLE, title);
			ConnectionFactory.getTemplateIndex().add(this.dataNode, IndexTypes.TEMPLATE_ID, templateId);
			ConnectionFactory.getTemplateIndex().add(this.dataNode, IndexTypes.TYPE, getClass().getName());
			ConnectionFactory.getDatabase().getReferenceNode().createRelationshipTo(dataNode, RelationTypes.MAIL_TEMPLATE_REFERENCE);
			tx.success();
		}
		catch (Exception e)
		{
			tx.failure();
		}
		finally
		{
			tx.finish();
		}
	}

	public long getId()
	{
		return getProperty(Long.class, ID, false);
	}

	public String getSubject()
	{
		return getProperty(String.class, SUBJECT);
	}

	public void setSubject(final String subject)
	{
		writeProperty(SUBJECT, subject);
	}

	public Node getDataNode()
	{
		return this.dataNode;
	}

	public String getText()
	{
		return getProperty(String.class, TEXT);
	}

	public void setText(final String text)
	{
		writeProperty(TEXT, text);
	}

	public String getHtml()
	{
		return getProperty(String.class, HTML);
	}

	public void setHtml(final String html)
	{
		writeProperty(HTML, html);
	}

	public String getTitle()
	{
		return getProperty(String.class, TITLE);
	}

	public void setTitle(final String title)
	{
		writeProperty(TITLE, title);
	}

	public static IndexHits<Node> getAll()
	{
		return ConnectionFactory.getTemplateIndex().get(IndexTypes.TYPE, MailTemplate.class.getName());
	}
}
