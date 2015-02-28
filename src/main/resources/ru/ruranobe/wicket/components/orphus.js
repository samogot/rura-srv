function showOrphusDialog(originalText, callbackUrl, startOffset, paragraph, chapterId)
{
	bootbox.dialog({
			title: "Предложить правку",
			message: '<form id="orphusForm">' +
					 '  <div class="form-group">' +
					 '    <label for="orphusReplacement">Текст правки</label>' +
					 '    <input type="text" class="form-control" name="replacementText" id="orphusReplacement" value="' + originalText + '">' +
					 '    <p class="help-block" style="display: none"></p>' +
					 '  </div>' +
					 '  <div class="form-group">' +
					 '    <label for="orphusComment">Комментарий <small>(необязательно)</small></label>' +
					 '    <input type="text" class="form-control" name="optionalComment" id="orphusComment">' +
					 '  </div>' +
					 '</form>',
			buttons: {
			   cancel: {
					label: "Отменить",
					className: "btn-default",
					callback: function () { }
				},
				success: {
					label: "Подтвердить",
					className: "btn-success",
					callback: function () {
/* TODO: text size check */                                            
						var replacement = $('#orphusReplacement').val();
						if (!replacement) {
							$('#orphusReplacement').next('.help-block').text('Введите текст для замены.').show();
							$('#orphusReplacement').parent('.form-group').addClass('has-error');
							return false;
						}
						if (replacement == originalText) {
							$('#orphusReplacement').next('.help-block').text('Текст для замены должен отличаться от исходного.').show();
							$('#orphusReplacement').parent('.form-group').addClass('has-error');
							return false;
						}
						var data = $('#orphusForm').serialize();
						var wcall = Wicket.Ajax.get({ 'u': callbackUrl + '&' + data + '&startOffset=' + startOffset + '&paragraph=' + paragraph + '&chapterId=' + chapterId + '&originalText=' + originalText});
					}
				}
			}
		}
	);
}

/* variable attrs come from wicket framework */
function isOrphusPreconditionsMet(attrs)
{
	if($('#orphusForm').length)
	{
		return false;
	}
	var keycode = Wicket.Event.keyCode(attrs.event);
	if ((keycode == 13) && (attrs.event.ctrlKey)) //ctrl+enter
	{
		var range = getSelectionRange();
		if (range && range.toString().length > 0)
		{
/* TODO: tag p must have associated 'id' attribute.
 *  Probably with special format like ch+chapterId+p+paragraph
 *  where paragraph is just порядковый номер абзаца :) */
			return $(range.startContainer).closest('p').is($(range.endContainer).closest('p'));
		}
	}
	return false;
}

function getSelectionRange()
{
	if (window.getSelection && window.getSelection().rangeCount > 0)
	{
		return window.getSelection().getRangeAt(0);
	}		
	else if (document.selection && document.selection.type != "Control")
	{
		return document.selection.createRange();
	}
	return null;
}

function getOrphusParameters()
{
	var range = getSelectionRange();
	var p = $(range.startContainer).closest('p').get(0);
	var offset = 0;
	if (range.cloneRange)
	{
		var preCaretRange = range.cloneRange();
		preCaretRange.selectNodeContents(p);
		preCaretRange.setEnd(range.endContainer, range.endOffset);
		offset = preCaretRange.toString().length;
	}
	else
	{
		var preCaretTextRange = document.body.createTextRange();
		preCaretTextRange.moveToElementText(p);
		preCaretTextRange.setEndPoint("EndToEnd", range);
		offset = preCaretTextRange.text.length;
	}
        /* TODO: add chapterId parameter. Maybe in wicket code */
	return {originalText: range.toString(), 
                startOffset: offset - range.toString().length, 
                paragraph: p.id};
}