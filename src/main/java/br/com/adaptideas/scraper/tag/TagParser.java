package br.com.adaptideas.scraper.tag;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import br.com.adaptideas.scraper.cleaner.Cleaner;
import br.com.adaptideas.scraper.cleaner.IrrelevantContentTagCleaner;
import br.com.adaptideas.scraper.cleaner.IrrelevantTagCleaner;
import br.com.adaptideas.scraper.cleaner.TagCleaner;
import br.com.adaptideas.scraper.matcher.TemplateTag;

/**
 * @author jonasabreu
 * 
 */
final public class TagParser {

	private final Pattern pattern = Pattern.compile("(?s)<([^<>]*?)>([^<>]*)");
	private final Cleaner cleaner;

	private static final Logger log = Logger.getLogger(TagParser.class);

	public TagParser(final List<TemplateTag> relevantElements) {
		List<TagCleaner> cleaners = new ArrayList<TagCleaner>();
		cleaners.add(new IrrelevantTagCleaner(relevantElements));
		cleaners.add(new IrrelevantContentTagCleaner(relevantElements));
		cleaner = new Cleaner(cleaners);
	}

	public TagParser() {
		cleaner = new Cleaner(new ArrayList<TagCleaner>());
	}

	public List<Tag> parse(final String template) {
		List<Tag> tags = new ArrayList<Tag>();
		Matcher matcher = pattern.matcher(template);

		while (matcher.find()) {
			String elementContent = null;

			if (matcher.group(2).length() != 0) {
				elementContent = matcher.group(2);
			}

			Tag tag = new TagReader().readTag(matcher.group(1), elementContent != null ? elementContent.trim() : null);
			tags.add(tag);

		}

		List<Tag> clean = cleaner.clean(tags);
		log.trace("Parsed html " + template + " and produced these tags: " + clean);
		return clean;
	}

}
