export async function updateUser(axios, firstName, lastName, email)
{
    return await axios.put(`api/Gateway/User`,
        {
            firstName,
            lastName,
            email
        });
}
export async function getUserRatings(axios)
{
    return await axios.$get(`api/Gateway/User/Ratings`);
}