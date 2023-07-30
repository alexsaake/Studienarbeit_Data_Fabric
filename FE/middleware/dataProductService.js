export async function getDataProducts(axios)
{
  return await axios.$get("api/Gateway/DataProducts");
}

export async function getDataProduct(axios, shortKey)
{
  return await axios.$get(`api/Gateway/DataProduct/${shortKey}`);
}

export async function getDataProductImage(axios, shortKey)
{
  return await axios.$get(`api/Gateway/DataProduct/${shortKey}/Image`);
}

export async function getDataProductData(axios, shortKey)
{
  return await axios.$get(`api/Gateway/DataProduct/${shortKey}/Data`);
}

export async function getDataProductRatings(axios, shortKey)
{
  return await axios.$get(`api/Gateway/DataProduct/${shortKey}/Ratings`);
}

export async function setDataProductRating(axios, shortKey, title, comment, rating)
{
  return await axios.$post(`api/Gateway/DataProduct/${shortKey}/Rating`,
      {
        title,
        comment,
        rating
      }
  );
}

export async function updateDataProductRating(axios, shortKey, title, comment, rating)
{
  return await axios.$put(`api/Gateway/DataProduct/${shortKey}/Rating`,
      {
        title,
        comment,
        rating
      }
  );
}

export async function deleteDataProductRating(axios, shortKey)
{
  return await axios.request(`api/Gateway/DataProduct/${shortKey}/Rating`, {data: null, method: 'delete'});
}

export async function getDataProductRatingCanSubmit(axios, shortKey)
{
  return await axios.$get(`api/Gateway/DataProduct/${shortKey}/Rating/CanSubmit`);
}

export async function getDataProductRatingMaxLengths(axios)
{
  return await axios.$get(`api/Gateway/DataProduct/Rating/MaxLengths`);
}