export default async function (axios)
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
