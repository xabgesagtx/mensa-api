<#if mensa??><b>${mensa.name}</b></#if>
<#if originalDate??>
${messagesService.getMessage("response.mensaIsClosed", originalDate, date)}

</#if>
<strong>${date}</strong>

<#if dishes?size == 0>
${messagesService.getMessage("response.noDishesAvailable")}
<#else>
<#list dishes as dish>
${dish.category}
<b>${dish.description}</b>
<i>${dish.labels?join(", ")}</i>
${dish.prices}

</#list>
</#if>
