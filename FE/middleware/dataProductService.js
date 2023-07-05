export async function getDataProducts(axios)
{
  try
  {
    return await axios.$get("api/Gateway/DataProducts");
  }
  catch (error)
  {
    console.log(error);
  }
}

export async function getDataProduct(axios, shortKey)
{
  try
  {
    return await axios.$get(`api/Gateway/DataProduct/${shortKey}`);
  }
  catch (error)
  {
    console.log(error);
  }
}


export async function getDataProductImage(axios, shortKey)
{
  try
  {
    return await axios.$get(`api/Gateway/DataProduct/${shortKey}/Image`);
  }
  catch (error)
  {
    console.log(error);
  }
}

export async function getDataProductData(axios, shortKey)
{
  try
  {
    return await axios.$get(`api/Gateway/DataProduct/${shortKey}/Data`);
  }
  catch (error)
  {
    console.log(error);
  }
}

export async function getDataProductRatings(axios, shortKey)
{
  try
  {
    return await axios.$get(`api/Gateway/DataProduct/${shortKey}/Rating`);
  }
  catch (error)
  {
    console.log(error);
  }
}

export async function setDataProductRating(axios, shortKey, title, comment, rating)
{
  return await axios.$put(`api/Gateway/DataProduct/${shortKey}/Rating`,
      {
        title,
        comment,
        rating
      }
  );
}

export async function getHasAlreadyRatedDataProduct(axios, shortKey)
{
  return await axios.$get(`api/Gateway/DataProduct/${shortKey}/HasAlreadyRated`);
}