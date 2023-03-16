export default async function (axios, shortKey)
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
