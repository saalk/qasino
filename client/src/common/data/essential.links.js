const linksDataFile = `
{
  "links": [
    {
      "title": "Blog",
      "caption": "Medium Blog lion",
      "icon": "chat",
      "url": "https://medium.com/ing-blog/ing-open-sources-lion-a-library-for-performant-accessible-flexible-web-components-22ad165b1d3d"
    },
    {
      "title": "Github ING",
      "caption": "github.com/ing-bank/lion",
      "icon": "code",
      "url": "https://github.com/ing-bank/lion"
    },
    {
      "title": "Storybook",
      "caption": "Lion on netlify",
      "icon": "school",
      "url": "https://lion-web-components.netlify.com/?path=/story/intro-lion-web-components--page"
    }
  ]
}
`;

const links = JSON.parse(linksDataFile);

export default links;
