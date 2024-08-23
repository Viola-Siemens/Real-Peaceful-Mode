package com.hexagram2021.real_peaceful_mode.common.manager.chat;

import com.hexagram2021.real_peaceful_mode.common.manager.Speaker;
import com.hexagram2021.real_peaceful_mode.common.manager.chat.selection.ChatSelection;

import javax.annotation.Nullable;
import java.util.Collection;

//Tree structure, usually link structure without selection.
public interface IChatMessage {
	String messageKey();
	Speaker speaker();

	@Nullable
	AbstractMessage getNext();
	@Nullable
	Collection<ChatSelection> getSelections();
}
