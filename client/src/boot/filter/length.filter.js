export default (value, size) => {
  if (!value) return '';
  value = value.toString();
  if (value.length <= size) {
    return value;
  }
  return `${value.substr(0, size)}...`;
};