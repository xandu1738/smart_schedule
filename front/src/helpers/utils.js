export function trimSlashes(str) {
	return str.replace(/^\/+|\/+$/g, "");
}

export function formatText(text) {
    if (!text || typeof text !== 'string') return '';

    return text
        .replace(/[_-]+/g, ' ') // convert _ and - to spaces
        .trim()
        .split(' ')
        .map(word => {
            if (word.toUpperCase() === word) {
                // treat full-uppercase acronyms like ID, API, etc.
                return word.charAt(0).toUpperCase() + word.slice(1).toLowerCase();
            }
            return word.charAt(0).toUpperCase() + word.slice(1);
        })
        .join(' ');
}