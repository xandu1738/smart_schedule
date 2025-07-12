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

export const formatDate = (date) => {
    if (!date || typeof date !== 'string') return '';
  
    const d = new Date(date);
    if (isNaN(d.getTime())) return '';
  
    const day = d.getDate();
    const month = d.toLocaleString('en-US', { month: 'short' });
    const year = d.getFullYear();
  
    const getOrdinal = (n) => {
      const s = ["th", "st", "nd", "rd"];
      const v = n % 100;
      return s[(v - 20) % 10] || s[v] || s[0];
    };
  
    return `${day}${getOrdinal(day)} ${month}, ${year}`;
};
  
